package model_grainshipping;

import core.*;
public class TestAssignment {
    private static final int NUM_ASSIGNMENTS = 10;
    private static Assignment Assignmentattached = null;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_ASSIGNMENTS; i++) {
            new Assignment("Assignment-" + i);
            //System.out.println("Assignment dauert wirklich : " + .gettimestep());
            EventQueue eq = EventQueue.getInstance();
            //Assignmentattached = (Assignment) event.getObjectAttached();
        }
        Assignmentattached = Assignment.Assignments.get(0);
        System.out.println("Assignment Capacity : " + Assignmentattached.getLoadingCapacity());
        Assignmentattached.lowerLoadingCapacity(100);
        System.out.println("Assignment Capacity : " + Assignmentattached.getLoadingCapacity());
    }
}

