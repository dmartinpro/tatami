package fr.ippon.tatami.domain;

/**
 * A group.
 */
public class Group implements Comparable<Group> {

    private String groupId;

    private boolean publicGroup;

    private String name;

    private String description;

    private String domain;

    private long counter;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(boolean publicGroup) {
        this.publicGroup = publicGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    @Override
    public int compareTo(Group o) {
        if (this.getName() == null) {
            return -1;
        }
        if (o.getName() == null) {
            return 1;
        }
        if (this.getName().equals(o.getName())) {
            return -1; // To display duplicates
        }
        return this.getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (!groupId.equals(group.groupId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return groupId.hashCode();
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", publicGroup=" + publicGroup +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", counter=" + counter +
                '}';
    }
}
