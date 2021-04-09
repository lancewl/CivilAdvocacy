package com.example.civiladvocacy;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Official implements Serializable {

    private String officeName;
    private String officialName;
    private String officialAddr;
    private String officialParty;
    private String officialPhone;
    private String officialUrl;
    private String officialEmail;
    private String officialPhoto;
    private String officialYt;
    private String officialFb;
    private String officialTw;

    Official(String officeName, String officialName, String officialAddr, String officialParty,
             String officialPhone, String officialUrl, String officialEmail, String officialPhoto,
             String officialYt, String officialFb, String officialTw) {
        this.officeName = officeName;
        this.officialName = officialName;
        this.officialAddr = officialAddr;
        this.officialParty = officialParty;
        this.officialPhone = officialPhone;
        this.officialUrl = officialUrl;
        this.officialEmail = officialEmail;
        this.officialPhoto = officialPhoto;
        this.officialYt = officialYt;
        this.officialFb = officialFb;
        this.officialTw = officialTw;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getOfficialAddr() {
        return officialAddr;
    }

    public String getOfficialParty() {
        return officialParty;
    }

    public String getOfficialPhone() {
        return officialPhone;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public String getOfficialPhoto() {
        return officialPhoto;
    }

    public String getOfficialYt() {
        return officialYt;
    }

    public String getOfficialFb() {
        return officialFb;
    }

    public String getOfficialTw() {
        return officialTw;
    }

    // TODO: delete unused

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    @NonNull
    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(getOfficeName());
            jsonWriter.name("content").value(getOfficialName());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
