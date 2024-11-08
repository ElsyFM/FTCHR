package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Motor Test",group="Linear OpMode")
//@Disabled
public class MotorTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftArmMotor = null;


    @Override
    public void runOpMode() {
        // Initialize hardware variables
        leftArmMotor = hardwareMap.get(DcMotor.class, "arm_left");

        leftArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait to press PLAY
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Keep running until the driver presses STOP
        while (opModeIsActive()) {

            double upperPowerLimit = 1;
            double lowerPowerLimit = -1;
            // Set motors to move with the Left Stick.
            double leftMotorPower = -gamepad1.left_stick_y;

            // Limit power Output
            if(leftMotorPower > upperPowerLimit ) // If its greater
            {
                leftMotorPower = upperPowerLimit;
            }
            if (leftMotorPower < lowerPowerLimit ) // If its smaller
            {
                leftMotorPower = lowerPowerLimit;
            }

            // Set Power
            leftArmMotor.setPower(leftMotorPower);

            // Show runtime & Power
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Left/Right", "%4.2f, %4.2f", leftMotorPower);
            telemetry.update();
        }
    }
}
