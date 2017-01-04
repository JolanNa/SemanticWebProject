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
    private int progessSaverEventYear;

    public ProgressSaver() {
        this.progressPromotions = 0;
        this.progressEvents = 0;
        this.progressStables = 0;
        this.progressTagTeams = 0;
        this.progressWorkers = 0;
        this.progressTitles = 0;
        this.progessSaverEventYear = 2016;
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
    public int getEventYearProgress() {
    	return progessSaverEventYear;
    }
    public void setEventYearProgress(int year) {
    	progessSaverEventYear = year;
    }
    
    public void setEventProgress(int p) {
    	progressEvents = p;
    }
    
    public void setProgress(String name, int progress) {
        switch(name) {
            case "Promotion":
                progressPromotions = progress;
                break;
            case "Event":
                progressEvents = progress;
                break;
            case "Stable":
                progressStables = progress;
                break;
            case "TagTeam":
                progressTagTeams = progress;
                break;
            case "Title":
                progressTitles = progress;
                break;
        }
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
    public int getProgressTitles() {
        return progressTitles;
    }
    
    public String toString() {
    	return "\n-- Progress Saver --\n"
    			+ "\nPromotions: " + progressPromotions
    			+ "\nEvents:     " + progressEvents
    			+ "\nStables:    " + progressStables
    			+ "\nTagTeams:   " + progressTagTeams
    			+ "\nTitles:   " + progressTitles
    			+ "\nWorkers:    " + progressWorkers;
    }
}
