package alirezajavadi.todotoday.model;

public class Todo {
    private String startFrom;
    private String endTo;
    private String jobTitle;
    private int isDone;
    private String date;
    private int isDisplayInList;
    private int idDatabase;

    public String getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(String startFrom) {
        this.startFrom = startFrom;
    }

    public String getEndTo() {
        return endTo;
    }

    public void setEndTo(String endTo) {
        this.endTo = endTo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int done) {
        isDone = done;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsDisplayInList() {
        return isDisplayInList;
    }

    public void setIsDisplayInList(int displayInList) {
        isDisplayInList = displayInList;
    }

    public int getIdDatabase() {
        return idDatabase;
    }

    public void setIdDatabase(int idDatabase) {
        this.idDatabase = idDatabase;
    }
}
