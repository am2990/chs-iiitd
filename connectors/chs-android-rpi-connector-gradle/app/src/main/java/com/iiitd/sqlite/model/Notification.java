package com.iiitd.sqlite.model;

/**
 * Created by apurv on 1/19/2016.
 */
public class Notification {

    int _id;
    int obs_id;
    String notification ;
    String created_at;

    public Notification(){

    }

    public Notification(String obsId, String notification) {
        this.obs_id = Integer.parseInt(obsId);
        this.notification = notification;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getObs_id() {
        return obs_id;
    }

    public void setObs_id(int obs_id) {
        this.obs_id = obs_id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String toString(){
        return this._id +":"+this.obs_id + "-" + this.created_at;
    }

}
