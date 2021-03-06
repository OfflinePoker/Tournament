package de.hatoka.group.capi.business;

import java.util.Collection;

public interface GroupBO
{
    /**
     * @return userRef of owner
     */
    String getOwner();

    /**
     * Removes the group
     */
    void remove();

    /**
     * @return name of group
     */
    String getName();

    /**
     * Create member for given user
     * @param userRef
     * @param name of member in side of the group
     * @return
     */
    MemberBO createMember(String userRef, String name);

    /**
     * @return members of the group (owner is one member)
     */
    Collection<MemberBO> getMembers();

    /**
     * @return identifier of group
     */
    String getID();

    /**
     * @param userRef
     * @return the member instance of the given user.
     */
    MemberBO getMember(String userRef);

    /**
     * @param userRef
     * @return true in case user is member of this group
     */
    boolean isMember(String userRef);
}
