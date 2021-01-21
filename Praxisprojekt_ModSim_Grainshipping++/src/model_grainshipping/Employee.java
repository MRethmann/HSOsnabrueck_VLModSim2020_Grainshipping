package model_grainshipping;

import core.SimulationObject;
import core.SimulationObjects;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Employee extends SimulationObject
{
    private String MitarbeiterID = null;
    private String name = null;
    private Integer sortVariable = 0;

    private long utilRestStart = 0l;
    private long restingSessionTime = 0l;
    private long totalRestingTime = 0l;
    private int restingSessions = 0;

    private long utilWorkStart = 0l;
    private long workingSessionTime = 0l;
    private long totalWorkingTime = 0l;
    private int workingSessions = 0;
    private Boolean currentlyWorking = false;

    public static final long DAILYWORKLOAD = 402; //hochgerechnet auf 7 Tage
    public static final long NEEDEDREST = 1038;

    public static ArrayList<Employee> Employees = new ArrayList<>(GrainShipping.getNumEmployees());
    private static long previousTimeStep = 0;

    public Employee(String MitarbeiterID, Integer sortVariable)
    {
        this.MitarbeiterID = MitarbeiterID;
        this.restingSessionTime = 5000;
        this.sortVariable = sortVariable;
        Employees.add(this);
    }

    public boolean checkDrivingPermission()
    {
        if ((this.workingSessionTime + GrainShipping.meanDurationPerRun) <= DAILYWORKLOAD)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public Boolean getCurrentlyWorking()
    {
        return currentlyWorking;
    }

    public String getMitarbeiterID()
    {
        return MitarbeiterID;
    }

    public Integer getSortVariable()
    {
        return sortVariable;
    }

    public void setUtilWorkStart(long timeStep)
    {
        utilWorkStart = timeStep;
    }

    public long getUtilWorkStart()
    {
        return utilWorkStart;
    }

    public int getWorkingSessions()
    {
        return workingSessions;
    }

    public void resetWorkingSessionTime()
    {
        workingSessionTime = 0l;
    }

    public void increaseWorkingSessions()
    {
        this.workingSessions++;
    }

    public Long getTotalWorkingTime()
    {
        return totalWorkingTime;
    }

    public void setCurrentlyWorking(Boolean currentlyWorking)
    {
        this.currentlyWorking = currentlyWorking;
    }

    public void setWorkingSessionTime(long workingTime)
    {
        this.workingSessionTime = workingTime;
    }

    public void increaseTotalWorkingTime(long totalWorkingTime)
    {
        this.totalWorkingTime += totalWorkingTime;
    }

    public void increaseWorkingSessionTime(long time)
    {
        workingSessionTime += time;
    }

    public int getRestingSessions()
    {
        return restingSessions;
    }

    public long getRestingSessionTime()
    {
        return restingSessionTime;
    }

    public void increaseRestingSessionTime(long time)
    {
        restingSessionTime += time;
    }

    public void resetRestingSessionTime()
    {
        restingSessionTime = 0l;
    }

    public void increaseRestingSessions()
    {
        restingSessions++;
    }

    public void increaseTotalRestingTime(long time)
    {
        totalRestingTime += time;
    }

    public long getTotalRestingTime()
    {
        return totalRestingTime;
    }

    public long getUtilRestStart()
    {
        return utilRestStart;
    }

    public void setUtilRestStart(long timeStep)
    {
        utilRestStart = timeStep;
    }


    @Override
    public boolean simulate(long timeStep) //ToDo: Überarbeiten
    {
        return false;
    }

    public void printWorkingStatistics(boolean append)
    {
        try
        {
            File workingStatistics = new File(this.name + ".txt");
            FileWriter myWriter = new FileWriter(this.name + ".txt",append);
            PrintWriter myPrinter = new PrintWriter(myWriter);
            myPrinter.println("FC Bayern");
            myPrinter.println("Bester Verein der Welt");
            myPrinter.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public static void printEmployees()
    {
        for (int i = 0; i < Employees.size(); i++)
            System.out.println(Employees.get(i).MitarbeiterID);
    }
}
