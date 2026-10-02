package com.fitness.service;

import com.fitness.model.Attendance;
import com.fitness.model.ClassBooking;
import com.fitness.model.Coach;
import com.fitness.model.Member;
import com.fitness.model.Membership;
import com.fitness.model.Payment;
import com.fitness.model.WorkoutClass;
import com.fitness.repository.JdbcFitnessRepositories;

import java.util.List;

public class FitnessService {

    private final JdbcFitnessRepositories repositories = new JdbcFitnessRepositories();

    public List<Member> getMembers() {
        return repositories.members.findAll();
    }

    public Member addMember(Member member) {
        return repositories.members.save(member);
    }

    public void updateMember(Member member) {
        repositories.members.update(member);
    }

    public void deleteMember(long id) {
        repositories.members.delete(id);
    }

    public List<Membership> getMemberships() {
        return repositories.memberships.findAll();
    }

    public Membership addMembership(Membership membership) {
        return repositories.memberships.save(membership);
    }

    public void updateMembership(Membership membership) {
        repositories.memberships.update(membership);
    }

    public void deleteMembership(long id) {
        repositories.memberships.delete(id);
    }

    public List<Coach> getCoaches() {
        return repositories.coaches.findAll();
    }

    public Coach addCoach(Coach coach) {
        return repositories.coaches.save(coach);
    }

    public void updateCoach(Coach coach) {
        repositories.coaches.update(coach);
    }

    public void deleteCoach(long id) {
        repositories.coaches.delete(id);
    }

    public List<WorkoutClass> getClasses() {
        return repositories.classes.findAll();
    }

    public WorkoutClass addClass(WorkoutClass workoutClass) {
        return repositories.classes.save(workoutClass);
    }

    public void updateClass(WorkoutClass workoutClass) {
        repositories.classes.update(workoutClass);
    }

    public void deleteClass(long id) {
        repositories.classes.delete(id);
    }

    public List<ClassBooking> getBookings() {
        return repositories.bookings.findAll();
    }

    public boolean isAlreadyBooked(long memberId, long classId) {
        return repositories.bookings.findAll()
                .stream()
                .anyMatch(booking ->
                        booking.getMemberId() == memberId
                                && booking.getClassId() == classId
                                && !"Цуцалсан".equals(booking.getStatus())
                );
    }

    public int getBookingCountForClass(long classId) {
        return (int) repositories.bookings.findAll()
                .stream()
                .filter(booking ->
                        booking.getClassId() == classId
                                && !"Цуцалсан".equals(booking.getStatus())
                )
                .count();
    }

    public ClassBooking addBooking(ClassBooking booking) {
        return repositories.bookings.save(booking);
    }

    public void updateBooking(ClassBooking booking) {
        repositories.bookings.update(booking);
    }

    public void deleteBooking(long id) {
        repositories.bookings.delete(id);
    }

    public List<Payment> getPayments() {
        return repositories.payments.findAll();
    }

    public Payment addPayment(Payment payment) {
        return repositories.payments.save(payment);
    }

    public void updatePayment(Payment payment) {
        repositories.payments.update(payment);
    }

    public void deletePayment(long id) {
        repositories.payments.delete(id);
    }

    public List<Attendance> getAttendances() {
        return repositories.attendances.findAll();
    }

    public Attendance addAttendance(Attendance attendance) {
        return repositories.attendances.save(attendance);
    }

    public void updateAttendance(Attendance attendance) {
        repositories.attendances.update(attendance);
    }

    public void deleteAttendance(long id) {
        repositories.attendances.delete(id);
    }

    public String getMemberName(long memberId) {
        return repositories.members.findById(memberId)
                .map(Member::getName)
                .orElse("Тодорхойгүй");
    }

    public String getCoachName(long coachId) {
        return repositories.coaches.findById(coachId)
                .map(Coach::getName)
                .orElse("Тодорхойгүй");
    }

    public String getClassName(long classId) {
        return repositories.classes.findById(classId)
                .map(WorkoutClass::getName)
                .orElse("Тодорхойгүй");
    }

    public String getBookingMemberName(long bookingId) {
        return repositories.bookings.findById(bookingId)
                .map(booking -> getMemberName(booking.getMemberId()))
                .orElse("Тодорхойгүй");
    }

    public int memberCount() {
        return getMembers().size();
    }

    public int activeMembershipCount() {
        return (int) getMemberships().stream()
                .filter(membership -> "Идэвхтэй".equals(membership.getStatus()))
                .count();
    }

    public int coachCount() {
        return getCoaches().size();
    }

    public int classCount() {
        return getClasses().size();
    }

    public int bookingCount() {
        return getBookings().size();
    }

    public double totalRevenue() {
        return getPayments().stream()
                .filter(payment -> "Төлөгдсөн".equals(payment.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}
