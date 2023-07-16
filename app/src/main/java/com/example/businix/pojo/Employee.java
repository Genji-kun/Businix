package com.example.businix.pojo;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee {
    @PropertyName("full_name")
    private String fullName;
    private Gender gender;
    private String phone;
    private String address;
    private Date dob;
    private String email;
    private String avatar;
    private String username;
    private String password;
    @PropertyName("create_at")
    private Date createAt;
    private Status status;
    private List<AbsentMail> absentMails;
    private List<Salary> salaries;

    private DocumentReference department;
    private DocumentReference position;

    public Employee() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DocumentReference getPosition() {
        return position;
    }

    public void setPosition(DocumentReference position) {
        this.position = position;
    }


    public DocumentReference getDepartment() {
        return department;
    }

    public void setDepartmentID(DocumentReference department) {
        this.department = department;
    }

    public void addAbsentMail(AbsentMail absentMail) {
        if (absentMails == null) {
            absentMails = new ArrayList<>();
        }
        absentMails.add(absentMail);
    }

    public void removeAbsentMail(AbsentMail absentMail) {
        if (absentMails != null) {
            absentMails.remove(absentMail);
        }
    }

    public void addSalary(Salary salary) {
        if (salaries == null) {
            salaries = new ArrayList<>();
        }
        salaries.add(salary);
    }

    public void removeSalary(Salary salary) {
        if (salaries != null) {
            salaries.remove(salary);
        }
    }
}
