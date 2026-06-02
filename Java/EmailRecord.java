package com.template;

public class EmailRecord {
    int id;
    String senderEmail;
    String receiverEmail;
    String subject;
    String body;
    String sentAt;
    boolean read;

    public EmailRecord(int id, String senderEmail, String receiverEmail, String subject,
                       String body, String sentAt, boolean read) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
        this.read = read;
    }

    public String toString() {
        return subject + " - " + sentAt;
    }
}
