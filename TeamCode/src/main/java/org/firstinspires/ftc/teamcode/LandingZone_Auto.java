package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

// AUTO
@Autonomous(name="Landing Zone Auto", group="Robot")
//@Disabled
public class LandingZone_Auto extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    // Drive Motors
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    // Arm
    private DcMotor armLeftMotor = null;


    @Override
    public void runOpMode(){
        // Initialize the hardware variables
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right");

        armLeftMotor = hardwareMap.get(DcMotor.class, "arm_left");



        // Set Direction
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        armLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);


        // Wait for driver to press PLAY
        telemetry.addData("Status", "Waiting for PLAY");
        telemetry.update();

        waitForStart();

        // Driver pressed PLAY
        runtime.reset();

        // This code will finish after 20 seconds.
        // It can be stopped at anytime if opMode is disabled.

        // For 0.2 seconds, go straight & lower the arm to the front.
        while (opModeIsActive() && (runtime.seconds() < 0.2))
        {
            // Between seconds: 0.0 - 0.2
            telemetry.addData("Status", "Run Time: " + runtime.toString());


            backRightDrive.setPower(0.5);
            backLeftDrive.setPower(0.5);
            telemetry.update();
        }
        // For 0.5 seconds, turn right.
        while (opModeIsActive() && (runtime.seconds() >= 0.2) && (runtime.seconds() < 1.2))
        {
            // Between seconds: 0.2 - 1.2
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            armLeftMotor.setPower(0.8);

            backLeftDrive.setPower(0.8);
            backRightDrive.setPower(-0.8);
            telemetry.update();

        }
        // For 15 seconds, do nothing.
        while (opModeIsActive() && (runtime.seconds() >= 1.2) && (runtime.seconds() < 16.0))
        {
            // Between seconds: 1.2 - 16.0
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            armLeftMotor.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
            telemetry.update();
        }
        // For 4 seconds, go straight.
        while (opModeIsActive() && (runtime.seconds() >= 16.0) && (runtime.seconds() < 20.0))
        {
            // Between seconds: 16.0 - 20.0
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            backLeftDrive.setPower(0.5);
            backRightDrive.setPower(0.5);
            telemetry.update();
        }
        // After 20 seconds, stop.
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);

        telemetry.addData("Auto", "Complete!");
        telemetry.update();
        sleep(1000);


    }
}
