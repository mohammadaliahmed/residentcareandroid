package com.siliconst.residentcare.NetworkResponses;

import com.siliconst.residentcare.Models.Department;
import com.siliconst.residentcare.Models.FaqsModel;
import com.siliconst.residentcare.Models.Notice;
import com.siliconst.residentcare.Models.Reply;
import com.siliconst.residentcare.Models.Ticket;
import com.siliconst.residentcare.Models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("tickets")
    @Expose
    private List<Ticket> tickets = null;
    @SerializedName("ticket")
    @Expose
    private Ticket ticket = null;
    @SerializedName("departments")
    @Expose
    private List<Department> departments = null;
    @SerializedName("notices")
    @Expose
    private List<Notice> notices = null;
    @SerializedName("replies")
    @Expose
    private List<Reply> replies = null;
    @SerializedName("faqs")
    @Expose
    private List<FaqsModel> faqs = null;
    @SerializedName("usercode")
    @Expose
    private String usercode = null;
    @SerializedName("admin_phone")
    @Expose
    private String admin_phone = null;

    public String getAdmin_phone() {
        return admin_phone;
    }

    public void setAdmin_phone(String admin_phone) {
        this.admin_phone = admin_phone;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public List<FaqsModel> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<FaqsModel> faqs) {
        this.faqs = faqs;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
