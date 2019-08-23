package ibox.iplanner.api.model;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

public class Event {

    private String id;
    @NotNull(message = "Summary {javax.validation.constraints.NotNull.message}")
    private String summary;
    private String description;

    @Valid
    @NotNull(message = "Creator {javax.validation.constraints.NotNull.message}")
    private User creator;
    @NotNull(message = "Created Time {javax.validation.constraints.NotNull.message}")
    private Instant created;
    private Instant updated;
    @NotNull(message = "Start Time {javax.validation.constraints.NotNull.message}")
    private Instant start;
    private Instant end;
    @NotNull(message = "Activity {javax.validation.constraints.NotNull.message}")
    private String activity;
    private String status;
    private String location;
    private Boolean endTimeUnspecified;
    private Set<String> recurrence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getEndTimeUnspecified() {
        return endTimeUnspecified;
    }

    public void setEndTimeUnspecified(Boolean endTimeUnspecified) {
        this.endTimeUnspecified = endTimeUnspecified;
    }

    public Set<String> getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Set<String> recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                ", created=" + created +
                ", updated=" + updated +
                ", start=" + start +
                ", end=" + end +
                ", activity='" + activity + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", endTimeUnspecified=" + endTimeUnspecified +
                ", recurrence=" + recurrence +
                '}';
    }
}
