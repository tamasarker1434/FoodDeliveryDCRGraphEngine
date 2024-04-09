import java.util.HashMap;
import java.util.HashSet;
public class Relations {
    // Relations
    public static HashMap<String, HashSet<String>> conditionsFor = new HashMap<String, HashSet<String>>();
    public static HashMap<String, HashSet<String>> milestonesFor = new HashMap<String, HashSet<String>>();
    public static HashMap<String, HashSet<String>> responsesTo = new HashMap<String, HashSet<String>>();
    public static HashMap<String, HashSet<String>> excludesTo = new HashMap<String, HashSet<String>>();
    public static HashMap<String, HashSet<String>> includesTo = new HashMap<String, HashSet<String>>();
    // Method to add a condition for an event
    public static void addConditionForEvent(){
        conditionsFor.put(Events.b, new HashSet<String>() {{
            add(Events.a);
        }});
        conditionsFor.put(Events.c, new HashSet<String>() {{
            add(Events.b);
        }});
        conditionsFor.put(Events.d, new HashSet<String>() {{
            add(Events.c);
        }});
        conditionsFor.put(Events.e, new HashSet<String>() {{
            add(Events.d);
        }});
        conditionsFor.put(Events.f, new HashSet<String>() {{
            add(Events.d);
        }});
        conditionsFor.put(Events.g, new HashSet<String>() {{
            add(Events.e);
            add(Events.f);
        }});
        conditionsFor.put(Events.h, new HashSet<String>() {{
            add(Events.g);
        }});
        conditionsFor.put(Events.i, new HashSet<String>() {{
            add(Events.g);
        }});
        conditionsFor.put(Events.j, new HashSet<String>() {{
            add(Events.b);
            add(Events.c);
            add(Events.g);
            add(Events.i);
        }});
        conditionsFor.put(Events.k, new HashSet<String>() {{
            add(Events.j);
        }});
        conditionsFor.put(Events.l, new HashSet<String>() {{
            add(Events.k);
        }});
        conditionsFor.put(Events.n, new HashSet<String>() {{
            add(Events.j);
        }});
        conditionsFor.put(Events.m, new HashSet<String>() {{
            add(Events.i);
        }});
        conditionsFor.put(Events.o, new HashSet<String>() {{
            add(Events.n);
        }});
        conditionsFor.put(Events.p, new HashSet<String>() {{
            add(Events.o);
        }});
        conditionsFor.put(Events.q, new HashSet<String>() {{
            add(Events.o);
        }});
        conditionsFor.put(Events.s, new HashSet<String>() {{
            add(Events.q);
        }});
        conditionsFor.put(Events.r, new HashSet<String>() {{
            add(Events.q);
        }});
        conditionsFor.put(Events.t, new HashSet<String>() {{
            add(Events.r);
        }});
        conditionsFor.put(Events.w, new HashSet<String>() {{
            add(Events.r);
        }});
        conditionsFor.put(Events.u, new HashSet<String>() {{
            add(Events.t);
        }});
    }
    // Method to add a responsesTo for an event
    public static void addResponsesToEvent(){
        responsesTo.put(Events.a, new HashSet<String>() {{
            add(Events.b);
        }});
        responsesTo.put(Events.b, new HashSet<String>() {{
            add(Events.c);
        }});
        responsesTo.put(Events.c, new HashSet<String>() {{
            add(Events.d);
        }});
        responsesTo.put(Events.d, new HashSet<String>() {{
            add(Events.e);
            add(Events.f);
        }});
        responsesTo.put(Events.e, new HashSet<String>() {{
            add(Events.g);
        }});
        responsesTo.put(Events.f, new HashSet<String>() {{
            add(Events.g);
        }});
        responsesTo.put(Events.g, new HashSet<String>() {{
            add(Events.i);
        }});
        responsesTo.put(Events.j, new HashSet<String>() {{
            add(Events.k);
            add(Events.n);
        }});
        responsesTo.put(Events.i, new HashSet<String>() {{
            add(Events.j);
        }});
        responsesTo.put(Events.k, new HashSet<String>() {{
            add(Events.l);
        }});
        responsesTo.put(Events.l, new HashSet<String>() {{
            add(Events.m);
        }});
        responsesTo.put(Events.n, new HashSet<String>() {{
            add(Events.o);
        }});
        responsesTo.put(Events.o, new HashSet<String>() {{
            add(Events.q);
        }});
        responsesTo.put(Events.q, new HashSet<String>() {{
            add(Events.r);
        }});
        responsesTo.put(Events.r, new HashSet<String>() {{
            add(Events.t);
        }});
        responsesTo.put(Events.t, new HashSet<String>() {{
            add(Events.u);
        }});
        responsesTo.put(Events.w, new HashSet<String>() {{
            add(Events.s);
            add(Events.t);
        }});
    }
    // Method to add a excludesTo for an event
    public static void addExcludesToEvent(){
        excludesTo.put(Events.e, new HashSet<String>() {{
            add(Events.f);
        }});
        excludesTo.put(Events.f, new HashSet<String>() {{
            add(Events.e);
        }});
        excludesTo.put(Events.n, new HashSet<String>() {{
            add(Events.k);
        }});
        excludesTo.put(Events.k, new HashSet<String>() {{
            add(Events.n);
        }});
        excludesTo.put(Events.t, new HashSet<String>() {{
            add(Events.w);
        }});
    }
}
