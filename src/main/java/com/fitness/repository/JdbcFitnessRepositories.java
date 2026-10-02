package com.fitness.repository;

import com.fitness.model.Attendance;
import com.fitness.model.ClassBooking;
import com.fitness.model.Coach;
import com.fitness.model.Member;
import com.fitness.model.Membership;
import com.fitness.model.Payment;
import com.fitness.model.WorkoutClass;

public final class JdbcFitnessRepositories {

    public final JdbcRepository<Member> members = new JdbcRepository<>(
            "members",
            new String[]{
                    "name", "phone", "email", "gender", "registration_date", "status"
            },
            rs -> new Member(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("gender"),
                    rs.getString("registration_date"),
                    rs.getString("status")
            ),
            member -> new Object[]{
                    member.getName(), member.getPhone(), member.getEmail(),
                    member.getGender(), member.getRegistrationDate(), member.getStatus()
            },
            Member::getId,
            Member::setId
    );

    public final JdbcRepository<Coach> coaches = new JdbcRepository<>(
            "coaches",
            new String[]{"name", "phone", "specialization", "status"},
            rs -> new Coach(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("specialization"),
                    rs.getString("status")
            ),
            coach -> new Object[]{
                    coach.getName(), coach.getPhone(), coach.getSpecialization(), coach.getStatus()
            },
            Coach::getId,
            Coach::setId
    );

    public final JdbcRepository<Membership> memberships = new JdbcRepository<>(
            "memberships",
            new String[]{
                    "member_id", "type", "start_date", "end_date", "price", "status"
            },
            rs -> new Membership(
                    rs.getLong("id"),
                    rs.getLong("member_id"),
                    rs.getString("type"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getDouble("price"),
                    rs.getString("status")
            ),
            membership -> new Object[]{
                    membership.getMemberId(), membership.getType(),
                    membership.getStartDate(), membership.getEndDate(),
                    membership.getPrice(), membership.getStatus()
            },
            Membership::getId,
            Membership::setId
    );

    public final JdbcRepository<WorkoutClass> classes = new JdbcRepository<>(
            "workout_classes",
            new String[]{
                    "name", "coach_id", "class_date", "class_time",
                    "room", "capacity", "status"
            },
            rs -> new WorkoutClass(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getLong("coach_id"),
                    rs.getString("class_date"),
                    rs.getString("class_time"),
                    rs.getString("room"),
                    rs.getInt("capacity"),
                    rs.getString("status")
            ),
            workoutClass -> new Object[]{
                    workoutClass.getName(), workoutClass.getCoachId(),
                    workoutClass.getDate(), workoutClass.getTime(),
                    workoutClass.getRoom(), workoutClass.getCapacity(),
                    workoutClass.getStatus()
            },
            WorkoutClass::getId,
            WorkoutClass::setId
    );

    public final JdbcRepository<ClassBooking> bookings = new JdbcRepository<>(
            "class_bookings",
            new String[]{"member_id", "class_id", "booking_date", "status"},
            rs -> new ClassBooking(
                    rs.getLong("id"),
                    rs.getLong("member_id"),
                    rs.getLong("class_id"),
                    rs.getString("booking_date"),
                    rs.getString("status")
            ),
            booking -> new Object[]{
                    booking.getMemberId(), booking.getClassId(),
                    booking.getBookingDate(), booking.getStatus()
            },
            ClassBooking::getId,
            ClassBooking::setId
    );

    public final JdbcRepository<Payment> payments = new JdbcRepository<>(
            "payments",
            new String[]{
                    "member_id", "amount", "payment_date", "method", "status", "description"
            },
            rs -> new Payment(
                    rs.getLong("id"),
                    rs.getLong("member_id"),
                    rs.getDouble("amount"),
                    rs.getString("payment_date"),
                    rs.getString("method"),
                    rs.getString("status"),
                    rs.getString("description")
            ),
            payment -> new Object[]{
                    payment.getMemberId(), payment.getAmount(), payment.getPaymentDate(),
                    payment.getMethod(), payment.getStatus(), payment.getDescription()
            },
            Payment::getId,
            Payment::setId
    );

    public final JdbcRepository<Attendance> attendances = new JdbcRepository<>(
            "attendance",
            new String[]{"booking_id", "attendance_date", "status"},
            rs -> new Attendance(
                    rs.getLong("id"),
                    rs.getLong("booking_id"),
                    rs.getString("attendance_date"),
                    rs.getString("status")
            ),
            attendance -> new Object[]{
                    attendance.getBookingId(), attendance.getDate(), attendance.getStatus()
            },
            Attendance::getId,
            Attendance::setId
    );
}
