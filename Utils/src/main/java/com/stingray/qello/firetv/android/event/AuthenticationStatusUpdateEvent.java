package com.stingray.qello.firetv.android.event;

/**
 * Event class to represent Authorization events.
 */
public class AuthenticationStatusUpdateEvent {

    /**
     * User authentication flag.
     */
    private boolean mUserAuthenticated = false;

    /**
     * Constructor
     *
     * @param flag User authentication flag.
     */
    public AuthenticationStatusUpdateEvent(boolean flag) {

        mUserAuthenticated = flag;
    }

    /**
     * Returns true if the user is authentication after this event happened, false otherwise
     *
     * @return true if the user is authentication after this event happened, false otherwise
     */
    public boolean isUserAuthenticated() {

        return mUserAuthenticated;
    }
}
