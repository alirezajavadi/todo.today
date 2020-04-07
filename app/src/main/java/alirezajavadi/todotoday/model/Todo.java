package alirezajavadi.todotoday.model;

public class Todo {
    private String startFrom;
    private String endTo;
    private String taskTitle;
    private int isDone;
    private String date;
    private int isDisplayInList;
    private int databaseId;
    private long reminderId;

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

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
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

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }
}
