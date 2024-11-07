package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TankDrive & Arm TeleOp",group="Linear OpMode")
//@Disabled
public class TankDrive_Arm_TeleOp extends LinearOpMode {
    // Elapsed Time
    private ElapsedTime runtime = new ElapsedTime();

    // Drive Motors
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    // Arm Motors
    private DcMotor armLeftMotor = null;
    private DcMotor armRightMotor = null;

    private Servo paperPlaneServo= null;

    @Override
    public void runOpMode(){
        // Initialize the hardware variables
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right");

        armLeftMotor = hardwareMap.get(DcMotor.class, "arm_left");
        armRightMotor = hardwareMap.get(DcMotor.class, "arm_right");

        paperPlaneServo = hardwareMap.get(Servo.class, "plane_servo");

        // Set Direction
        backLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        armLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        armRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set/Reset Servo Position (Unsure on if this works?)
        paperPlaneServo.setPosition(0);

        // Wait for driver to press PLAY
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Run until the drives presses STOP
        while (opModeIsActive())
        {
            /* Work on code for Tank Drive, then re-implement the Arm
            code to work with triggers instead of the joystick. */
                /*   TANK DRIVE   */
            // Set the Wheel power to move with joystick Y (left & right respectively).
            double backLeftDrivePower = -gamepad1.left_stick_y;
            double backRightDrivePower = -gamepad1.right_stick_y;

            // Set Power
            backLeftDrive.setPower(backLeftDrivePower);
            backRightDrive.setPower(backRightDrivePower);

            // Show Runtime and Power
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Back Motor Power, Left/Right", "%4.2f, $4.2f",
                            backLeftDrivePower, backRightDrivePower);
                /*   ARM   */

            // Set the Arm Power to move a set amount,
            // depending on if the respective trigger is held.

            // Triggers are read as a float from 0-1 depending on force.
            // The right trigger will bring the arm down.
            // The left trigger will bring the arm up.
            double arm_DownMax = 1.00; // Max value the Arm can go downwards
            double arm_DownTuningMax = 0.25; // Max value that the arm could be fine tuned. (down)

            double arm_UpMax = 1.00; // Max value the Arm can go Upwards
            double arm_UpTuningMax = 0.25; // Max value that the arm could be fine tuned. (up)

            boolean left_bumper = gamepad2.left_bumper;
            boolean right_bumper = gamepad2.right_bumper;

            double left_trigger = gamepad2.left_trigger;
            double right_trigger = gamepad2.right_trigger;

            // Use bumpers to be able to fine tune the robot's arm.

            // If no triggers are being pressed:
            if (right_trigger == 0 && left_trigger == 0)
            {
                // If the right bumper is pressed AND the left is not:
                if(right_bumper && !left_bumper)
                {
                    armLeftMotor.setPower(arm_DownTuningMax);
                    armRightMotor.setPower(arm_DownTuningMax);
                }
                // If the left bumper is pressed AND the right is not:
                if(left_bumper && !right_bumper)
                {
                    armLeftMotor.setPower(-arm_UpTuningMax);
                    armRightMotor.setPower(-arm_UpTuningMax);
                }

            }
            //telemetry.addData("Bumpers, Left/Right", "%4.2f, %4.2f",
                    //left_bumper, right_bumper);

            // If the right trigger is down AND the left is not pressed:
            if (right_trigger > 0 && left_trigger == 0)
            { // Bring Down
                if (right_trigger > arm_DownMax) // If it exceeds a set Max
                {
                    right_trigger = arm_DownMax; // Set power to Max
                }
                armLeftMotor.setPower(right_trigger);
                armRightMotor.setPower(right_trigger);
            }
            // Vice versa
            if (left_trigger > 0 && right_trigger == 0)
            { // Bring Up
                if (left_trigger > arm_UpMax) // If it exceeds a set Max
                {
                    left_trigger = arm_UpMax;
                }
                armLeftMotor.setPower(-left_trigger);
                armRightMotor.setPower(-left_trigger);
            }

            telemetry.addData("Triggers, Left/Right", "%4.2f, %4.2f",
                            left_trigger, right_trigger);

            /*   SERVO   */
            // At a button press, move the  90 degrees.//
            // Currently bugged. Once it gets to 90 degrees, it stays at 90.
            // Whenever able to, set the servo to reset its position.
            boolean button_A = gamepad1.a; // If 'A' pressed or not.
            boolean A_NotPressed = true; // If 'A' was ever pressed.


            if (button_A && A_NotPressed) // 'A' is pressed and it wasn't pressed before.
            {
                // Move 90 degrees
                paperPlaneServo.setPosition(-90);
                A_NotPressed = false;
                paperPlaneServo.setPosition(0);
            }


            // Update Data on App
            telemetry.update();
        }
    }
}
