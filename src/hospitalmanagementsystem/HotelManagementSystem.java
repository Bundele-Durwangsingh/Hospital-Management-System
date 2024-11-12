package hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="admin";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdb.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        Scanner input = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            patients patients = new patients(connection, input);
            doctor doctor = new doctor(connection);
            while (true) {
                System.out.println("1. Add Patients");
                System.out.println("2. View Patints");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");
                int choice= input.nextInt();

                
                switch(choice){
                    case 1:
                        patients.addPatients();
                        break;
                    case 2:
                        patients.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patients, doctor, connection, input);
                        break;
                    case 5:
                        exit();
                        return;
                    default:
                        System.out.println("Entre valid input");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static void bookAppointment(patients patients,doctor doctor, Connection connection, Scanner input){
        System.out.print("Enter patients id : ");
        int patientId= input.nextInt();
        System.out.print("Enter doctor Id : ");
        int doctorId = input.nextInt();
        System.out.println("Enter appointment date(yyyy-mm-dd) : ");
        String appointment= input.next();

        if(patients.getPatientById(patientId)&& doctor.getDoctorById(doctorId)){
            if(checkdoctor(doctorId,appointment,connection)){
                String appointmentquery ="INSERT INTO appointments(patient_id,doctor_id ,appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentquery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointment);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Sorry doctor is not avaiable");
            }
        }else{
            System.out.println("Somthing went wrong !! please check patient or doctor id");
        }
    }
    public static boolean checkdoctor(int doctorId,String appointment ,Connection connection ){
        String query ="SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointment);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hospital Management System!!!");
    }
}
