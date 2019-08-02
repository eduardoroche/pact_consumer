package io.reflectoring;

public class UserCreatedMessage {

    private String messageUuid;

    private UserMessage user;

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public UserMessage getUser() {
        return user;
    }

    public void setUser(UserMessage usert) {
        user = usert;
    }
}
