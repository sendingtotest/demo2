package io.javabrains.demo4.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders_tbl")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;

    private String ordername;
    private String orderDepartment;

    public Order() {
    }

    public Order(Long orderid, String ordername, String orderDepartment) {
        this.orderid = orderid;
        this.ordername = ordername;
        this.orderDepartment = orderDepartment;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public String getOrdername() {
        return ordername;
    }

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

    public String getOrderDepartment() {
        return orderDepartment;
    }

    public void setOrderDepartment(String orderDepartment) {
        this.orderDepartment = orderDepartment;
    }
}
