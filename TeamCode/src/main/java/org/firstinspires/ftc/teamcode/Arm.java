package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@Config
@TeleOp
@Disabled
public class Arm extends OpMode {
    private PIDController controller;
    private GripperTest gripperTest;

    public static double p = 0.005, i = 0, d= 0.001;
    public static double f = 0.22;

    public static int target;

    public static int originalArmPos;



    public static double ticks_in_degrees = (double) 360/(28*230*4*231);

    private DcMotorEx arm_motor;

    @Override
    public void init() {

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        arm_motor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        originalArmPos = arm_motor.getCurrentPosition() ;
        controller = new PIDController(p, i, d);

        gripperTest = new GripperTest(hardwareMap);

    }

    @Override
    public void loop() {

       // target = arm_motor.getCurrentPosition()+5;

        arm_motor.setDirection(DcMotorSimple.Direction.REVERSE);
        controller.setPID(p, i, d);

        //float target = -gamepad1.left_trigger * 100;
        int armPos = arm_motor.getCurrentPosition();

        if (gamepad1.dpad_down) {

            for(int i=0;i<2000;i++) {
                target = 2000 - i;
            }
        }
        if (gamepad1.dpad_up) {

            //arm_motor.setPower(0.1);
            for(int i=originalArmPos;i<9000;i++) {
                target = originalArmPos + i;
            }
//            target = originalArmPos+Math.abs(originalArmPos*10);
        }




        double pid = controller.calculate(armPos, target);
        double ff = Math.cos(Math.toRadians(target/ticks_in_degrees)) * f;

        double power = 0.5*(pid + ff);

        arm_motor.setPower(power);
        try {
            gripperTest.handleServo(gamepad2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        telemetry.addData("pos", armPos);
        telemetry.addData("target", target);
        telemetry.update();




    }

}
