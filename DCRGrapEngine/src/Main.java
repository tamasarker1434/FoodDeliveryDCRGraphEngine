import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    //Customer Activity Tracking
    public static HashSet<String> customerActivity= new HashSet<>();
    public static void initializeGraph(){
        addEvent();
        addFinalStates();
        AddCustomerActivity();
        Relations.addConditionForEvent();
        Relations.addResponsesToEvent();
        Relations.addExcludesToEvent();
    }

    private static void AddCustomerActivity() {
        customerActivity.add("food delivery app");
        customerActivity.add("select food");
        customerActivity.add("provide address");
        customerActivity.add("pays for food");
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
    private static LocalDateTime lastActivityTime;
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
        }
        Duration duration = Duration.between(lastActivityTime, LocalDateTime.now());
        long minutesTotal = duration.toMinutes();
        if (minutesTotal > 65) {
            ExecuteSingleEvent(Events.v);
        }
        System.out.println("\n" +"Traces: " + traces+"\n" );
        System.out.println("Check the enabledness of Events!");
        for (String item : events){
            if (enabled(item))
                System.out.println("\n" + item + " Event is enabled!");
            else
                System.out.println("\n" + item + " Event is not enabled!");
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
            if (!Relations.conditionsFor.containsKey(event) && !Objects.equals(event, Events.v))
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
        if (lastActivityTime == null)
            lastActivityTime = LocalDateTime.now();
        else{
            Duration duration = Duration.between(lastActivityTime, LocalDateTime.now());
            long minutesInactive = duration.toMinutes();
            if (customerActivity.contains(event) && minutesInactive > 10){
                for (String s : customerActivity) {
                    // Check if the event is already present in the pending HashMap
                    pending.remove(s);
                    excluded.remove(s);
                    included.remove(s);
                }
                pending.addAll(customerActivity);
            }
            else {
                if (customerActivity.contains(Events.g))
                    lastActivityTime = LocalDateTime.now();
            }
        }
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