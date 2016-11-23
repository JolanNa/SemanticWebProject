package progressSaver;

/**
 * Created by Jolan on 23/11/16.
 */
public class ProgressSaver implements java.io.Serializable {
    private int progressPromotions;
    private int progressEvents;
    private int progressStables;
    private int progressTagTeams;
    private int progressWorkers;
    private int progressTitles;

    public ProgressSaver() {
        this.progressPromotions = 0;
        this.progressEvents = 0;
        this.progressStables = 0;
        this.progressTagTeams = 0;
        this.progressWorkers = 0;
    }

    public int getProgress(String name) {
        switch(name) {
            case "Promotion":
                return progressPromotions;
            case "Event":
                return progressEvents;
            case "Stable":
                return progressStables;
            case "TagTeam":
                return progressTagTeams;
            case "Title":
                return progressTitles;
        }
        return 0;
    }

    public int getProgressPromotions() {
        return progressPromotions;
    }

    public int getProgressEvents() {
        return progressEvents;
    }

    public int getProgressStables() {
        return progressStables;
    }

    public int getProgressTeams() {
        return progressTagTeams;
    }

    public int getProgressWorkers() {
        return progressWorkers;
    }
}
