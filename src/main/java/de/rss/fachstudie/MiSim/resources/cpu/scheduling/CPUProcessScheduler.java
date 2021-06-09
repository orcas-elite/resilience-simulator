package de.rss.fachstudie.MiSim.resources.cpu.scheduling;


import de.rss.fachstudie.MiSim.export.MultiDataPointReporter;
import de.rss.fachstudie.MiSim.resources.cpu.CPUProcess;
import org.javatuples.Pair;

/**
 * Superclass that provides the interface for all CPU scheduling strategies.
 *
 * @see CPUProcess
 */
public abstract class CPUProcessScheduler {

    /**
     * Predefined reporter.
     */
    protected MultiDataPointReporter reporter;

    public CPUProcessScheduler(String name) {
        reporter = new MultiDataPointReporter(name);
    }

    /**
     * Enters the process into the scheduling queue.
     *
     * @param process {@code CPUProcess} that is to be scheduled
     * @see CPUProcess
     */
    public abstract void enterProcess(CPUProcess process);

    /**
     * Pulls the next {@code CPUProcess} to handle and its assigned time/work quantum.
     * <p>
     * For more complicated schedulers like a {@link RoundRobinScheduler} or {@link MLFQScheduler} this method does
     *
     * @return a pair containing the next {@code CPUProcess} to handle and its assigned time/work quantum.
     * @see CPUProcess
     */
    public abstract Pair<CPUProcess, Integer> retrieveNextProcess();


    /**
     * Pulls the next {@code CPUProcess} to handle and its assigned time/work quantum.<br> Prevents automatic
     * rescheduling of the process like in round robin scheduling.
     * <p>
     * This method is used to offer scheduling for multithreading. But requires manual rescheduling of unfinished
     * processes.
     *
     * @return a pair containing the next {@code CPUProcess} to handle and its assigned time quantum.
     * @see CPUProcess
     */
    public abstract Pair<CPUProcess, Integer> retrieveNextProcessNoReschedule();

    /**
     * @return true if there is a thread ready to schedule, false otherwise
     */
    public abstract boolean hasThreadsToSchedule();

    /**
     * @return the sum of the demand remainder of all processes that are currently in queue.
     */
    public abstract int getTotalWorkDemand();

    /**
     * Clears all current processes from the scheduler
     */
    public abstract void clear();

    public abstract int size();
}