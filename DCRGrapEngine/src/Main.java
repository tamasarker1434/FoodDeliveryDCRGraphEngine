import java.util.*;
public class Main {
    static Scanner scanner = new Scanner(System.in);
    // Events
    public static HashSet<String> events = new HashSet<String>();
    public static HashSet<String> finalStates = new HashSet<String>(){};
    //Traces
    private static Queue<String> traces= new LinkedList<>();
    //DCR Graph Marking
    private static HashSet<String> executed = new HashSet<String>();
    private static HashSet<String> included = new HashSet<String>();
    private static HashSet<String> pending = new HashSet<String>();
    private static HashSet<String> excluded = new HashSet<String>();
    private static HashSet<String> tempEvents = new HashSet<String>();
    public static void initializeGraph(){
        addEvent();
        addFinalStates();
        Relations.addConditionForEvent();
        Relations.addResponsesToEvent();
        Relations.addExcludesToEvent();
    }
    private static void addFinalStates() {
        finalStates.add("get refund");
        finalStates.add("successful delivery");
    }
    // Method to add an event to the graph
    public static void addEvent() {
        for (java.lang.reflect.Field field : Events.class.getDeclaredFields()) {
            if (field.getType() == String.class) {
                try {
                    String event = (String) field.get(null);
                    events.add(event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String label = "";
    public static void main(String[] args) {
        initializeGraph();
        tempEvents = new HashSet<>(events);
        System.out.println("Hello to DCR Engine!");
        AddPendingEvents();
        while (!tempEvents.isEmpty() || !pending.isEmpty()){
            if (!pending.isEmpty()){
                ExecutePending();
            }
            else {
                for (Map.Entry<String, HashSet<String>> entry : Relations.conditionsFor.entrySet()) {
                    if (entry.getValue().contains(label)) {
                        label = entry.getKey();
                        pending.add(label);
                    }
                }
                ExecutePending();
            }
            System.out.println("Traces: " + traces);
        }
        System.out.println("executed events: " + executed);
        System.out.println("included events: " + included);
        System.out.println("Pending events: " + pending);
        System.out.println("excluded events: " + excluded);
        System.out.println("TempEvents: " + tempEvents);
        System.out.println("Events: " + events);
        System.out.println("Traces: " + traces);
        System.out.println("Check the enabledness of Events!");
        for (String item : events){
            if (enabled(item))
                System.out.println("\n" + item + " Event is enabled!\n");
            else
                System.out.println("\n" + item + " Event is not enabled!\n");
        }
    }
    private static void ExecutePending() {
        if (!pending.isEmpty()){
            int index = 0;
            String[] array = new String[pending.size()];
            for (String i : pending){
                array[index++] = i;
            }
            index = 0;
            System.out.println("Pending events: ");
            for (String i : array){
                System.out.println(index++ +". " + i);
            }
            System.out.println("Choose to Execute event: ");
            label = scanner.nextLine();
            index = Integer.parseInt(label);
            ExecuteEvents(array[index]);
        }
    }
    private static void AddPendingEvents() {
        for (String event : tempEvents){
            if (!Relations.conditionsFor.containsKey(event))
                pending.add(event);
        }
    }
    private static void ExecuteEvents(String event) {
        included.add(event);
        HashSet<String> conditions = CheckConditions(event);
        HashSet<String> excludes = Relations.excludesTo.getOrDefault(event, new HashSet<>());
        HashSet<String> responses = Relations.responsesTo.getOrDefault(event, new HashSet<>());
        if (!conditions.isEmpty()){
            for (String condition : conditions){
                if (tempEvents.contains(condition))
                    pending.add(condition);
                else {
                    if (pending.contains(condition))
                        ExecuteSingleEvent(condition);
                }
            }
        }
        ExecuteSingleEvent(event);
        if(!excludes.isEmpty()){
            for (String excludedE : excludes){
                excluded.add(excludedE);
                tempEvents.remove(excludedE);
                pending.remove(excludedE);
            }
        }
        if (!responses.isEmpty()){
            for (String responseE : responses){
                if (!CheckConditions(responseE).isEmpty())
                    pending.add(responseE);
                else {
                    ExecuteSingleEvent(responseE);
                }
            }
        }
    }
    private static void ExecuteSingleEvent(String event) {
        executed.add(event);
        pending.remove(event);
        tempEvents.remove(event);
        traces.add(event);
        if (finalStates.contains(event)){
            included.addAll(tempEvents);
            tempEvents.clear();
            pending.clear();
        }
        else {
            Set<String> incmil = Relations.milestonesFor.get(event);
            if (incmil != null) {
                pending.addAll(incmil);
            }
        }
    }
    private static HashSet<String> CheckConditions(String event){
        return Relations.conditionsFor.getOrDefault(event, new HashSet<>());
    }
    private static Boolean enabled(String event) {
        // Open world assumption: if an event doesn't exist in the graph it must be enabled.
        if (!tempEvents.contains(event)) {
            return true;
        }
        // Check if the event is included
        if (!included.contains(event)) {
            return false;
        }
        // Select only the included conditions
        Set<String> inccon = Relations.conditionsFor.get(event);
        if (inccon == null) {
            // Handle the case where conditions are not defined for the event
            return false;
        }
        inccon = new HashSet<>(inccon); // Create a copy to avoid modifying the original set
        inccon.retainAll(included);

        // Check if all included conditions have been executed
        if (!executed.containsAll(inccon)) {
            return false;
        }
        // Select only the included milestones
        Set<String> incmil = Relations.milestonesFor.get(event);
        if (incmil == null) {
            // Handle the case where milestones are not defined for the event
            return false;
        }
        incmil = new HashSet<>(incmil); // Create a copy to avoid modifying the original set
        incmil.retainAll(included);
        // Check if any included milestone has a pending response
        for (String p : pending) {
            if (incmil.contains(p)) {
                return false;
            }
        }
        return true;
    }
}