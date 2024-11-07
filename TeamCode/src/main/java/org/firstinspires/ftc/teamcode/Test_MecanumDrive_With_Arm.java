package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Mecanum Drive & Arm Test", group="Linear OpMode")
//@Disabled
public class Test_MecanumDrive_With_Arm extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    /*      Arm Motors      */
    // Two REV Motors (90 Degrees) & One Servo Motor
    // Rev Motors (90) mean these motors will perform as normal, yet are at a physical 90 degree angle.
    private DcMotor armMotor_Base = null;
    private DcMotor armMotor_Middle = null;
    private Servo clawMotor = null;
    // Arm Limit Constants
    private static final double ARM_MOTOR_BASE_POWER_LIMIT = 0.0;
    private static final int ARM_MOTOR_BASE_ROTATIONAL_LIMIT = 0;
    private static final double ARM_MOTOR_MIDDLE_POWER_LIMIT = 0.0;
    private static final int ARM_MOTOR_MIDDLE_ROTATIONAL_LIMIT = 0;
    private static final double CLAW_MOTOR_POWER_LIMIT = 0.0;
    private static final int CLAW_MOTOR_ROTATIONAL_LIMIT = 0;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        armMotor_Base = hardwareMap.get(DcMotor.class, "arm_motor_base");
        armMotor_Middle = hardwareMap.get(DcMotor.class, "arm_motor_middle");
        //clawMotor = hardwareMap.get(Servo.class, "claw_motor");

        /*
         ########################################################################################
         !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
         ########################################################################################
         Most robots need the motors on one side to be reversed to drive forward.
         The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
         If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
         that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
         when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
         Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
         Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        */
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //region Drive Power Logic
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

            /*
            leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            //endregion

            //region Set Arm Limits & Power
            /*  Arm Controls (Using gamepad2/Second Controller Port)
            *   Each motor will use a different set of controls.
            * Base Motor: Left Joystick
            * Middle Motor: Right Joystick
            * Claw Motor: D-Pad
            *
            *   Arm Limits
            *   The base motor will have the harshest/low-number limit.
            *   The middle motor will have slightly more than the base motor.
            *   The claw motor will have the most control, over the middle motor.
            *
            *   Arm Power
            *   Similar to limits, the base will have the lowest power (normally).
            *   Middle motor = Slightly more than Base.
            *   Claw motor = Slightly more than Middle.
            */


            // Arm Power
            //
            double gamepad2_leftStickPower = gamepad2.left_stick_y; // [-1 to 1], -1 being reverse, 0 = none.
            double gamepad2_rightStickPower = gamepad2.right_stick_y; // [-1 to 1]
            boolean gamepad2_dPadUp_Pressed = gamepad2.dpad_up; // True if pressed, false otherwise
            boolean gamepad2_dPadDown_Pressed = gamepad2.dpad_down; // True if pressed, false otherwise

            //
            double armMotor_BasePower = gamepad2_leftStickPower; // Change to ternary to account for limit later
            double armMotor_MiddlePower = gamepad2_rightStickPower; // Change to ternary to account for limit later


            //armMotor_Base.setPower(armMotor_BasePower);
            //armMotor_Middle.setPower(armMotor_MiddlePower);


            // Arm Rotational Limits
            // Base Motor Limit = 180 Degrees?
            //armMotor_Base.getCurrentPosition();

            // You can't set a Power for servos, so track position.
            double clawMotor_Position = 0.0; // [0 to 1], the closer to 1, the closer to a 360* turn.
            //clawMotor.setPosition(clawMotor_Position);


            //endregion

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            // Show Arm Details
            telemetry.addData("Arm: Base Motor (Power): ", "%4.2f", armMotor_BasePower);
            telemetry.addData("Arm: Base Motor (Position): ", "%4.2f", armMotor_Base.getCurrentPosition());
            telemetry.addData("Arm: Middle Motor (Power): ", "%4.2f", armMotor_MiddlePower);

            //telemetry.addData("Arm: Claw Motor (Position): ", "%4.2f", clawMotor_Position);
            telemetry.update();
        }
    }
}

