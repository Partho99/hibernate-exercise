package com.example.hibernateexercise;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@AllArgsConstructor
public class RestExample {


    private final SessionFactory sessionFactory;

    @GetMapping("api/")
    public Account ShowString() {
        Transaction transaction;
        Session session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        AccountRole accountRole = new AccountRole();
        accountRole.setName("Accountant");
        Long s = (Long) session.save(accountRole);
        accountRole.setId(s);
        Account account = new Account();
        account.setAccountRole(accountRole);
        account.setAccountHolder("aminul");
        session.save(account);
//        transaction.commit();
//        session.close();

        return account;
    }

    @GetMapping("api2/")
    public Person getPerson() {
        Transaction transaction;
        Session session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        Person person = new Person();
        person.setName("partho");
        List<Address> addressList = new ArrayList<>();
        addressList.add(new Address("ROAD NO : 12A", 123, "Dhaka", 1212, person));
        addressList.add(new Address("ROAD NO : 12B", 124, "Dhaka", 1212, person));
        addressList.add(new Address("ROAD NO : 12C", 125, "Dhaka", 1212, person));
        person.setAddresses(addressList);
        session.save(person);
        transaction.commit();
        return person;
    }

    @GetMapping("api3/")
    public Person deletePerson() {
        Transaction transaction;
        Session session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        Person person = session.find(Person.class, 5);
        Address address = new Address();
        address.setPerson(null);
//        person.getAddresses().remove(address);
        session.delete(person);
        transaction.commit();
        return person;
    }

    @GetMapping("api4/")
    @Transactional
    public Person whenParentSavedThenMerged() {
        Transaction transaction;
        Session session = sessionFactory.getCurrentSession();
//      transaction = session.beginTransaction();
        Person person = new Person();
        person.setName("Admin");

        Address address = new Address();
        address.setPerson(person);
        address.setCity("Chittagong");
        address.setHouseNumber(123);
        address.setStreet("12A");
        address.setZipCode(1212);
        person.setAddresses(List.of(address));
        session.persist(person);

        System.out.println("flushing started1");
        session.flush();
        System.out.println("flushing ended1");

        int addressId = address.getId();
        session.clear();

        Address savedAddressEntity = session.find(Address.class, addressId);
        Person savedPersonEntity = savedAddressEntity.getPerson();
        savedPersonEntity.setName("Administrator");
        savedAddressEntity.setHouseNumber(24);
        session.merge(savedPersonEntity);

        System.out.println("flushing started2");
        session.flush();
        System.out.println("flushing ended2");

//      transaction.commit();
        return person;
    }

}
