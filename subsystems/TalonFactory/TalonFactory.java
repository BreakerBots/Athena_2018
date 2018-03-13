package org.usfirst.frc.team5104.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonFactory {
	TalonSRX talons[];
	
	public TalonFactory(int[] talonIds) {
		talons = new TalonSRX[talonIds.length];
		for (int i = 0; i < talonIds.length; i++) {
			talons[i] = new TalonSRX(talonIds[i]);
		}
	}
	
	public void init() {
		for (TalonSRX taloni:talons) {
			taloni.configOpenloopRamp(0, 0);
			taloni.configClosedloopRamp(0, 0);
			taloni.configPeakOutputForward(1, 0);
			taloni.configPeakOutputReverse(-1, 0);
			taloni.configNominalOutputForward(0, 0);
			taloni.configNominalOutputReverse(0, 0);
			taloni.configNeutralDeadband(0.04, 0);
			taloni.configVoltageCompSaturation(0, 0);
			taloni.configVoltageMeasurementFilter(32, 0);
			taloni.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
			//taloni.configSelectedFeedbackSensor(RemoteFeedbackDevice.None, 0, 0);
			//taloni.configSelectedFeedbackCoefficient(1.0);
			//taloni.configRemoteFeedbackFilter(off 0);
			//taloni.configSensorTerm	Quad (0) for all term types);
			taloni.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, 0);
			taloni.configVelocityMeasurementWindow(64, 0);
			//taloni.configReverseLimitSwitchSource(0, and "Normally Open");
			//taloni.configForwardLimitSwitchSource(LimitSwitchSource.?, LimitSwitchNormal.NormallyOpen, 0);
			taloni.configForwardSoftLimitThreshold(0, 0);
			taloni.configReverseSoftLimitThreshold(0, 0);
			taloni.configForwardSoftLimitEnable(false, 0);
			taloni.configReverseSoftLimitEnable(false, 0);
			taloni.config_kP(0, 0, 0);
			taloni.config_kI(0, 0, 0);
			taloni.config_kD(0, 0, 0);
			taloni.config_kF(0, 0, 0);
			taloni.config_IntegralZone(0, 0, 0);
			taloni.configAllowableClosedloopError(0, 0, 0);
			taloni.configMaxIntegralAccumulator(0, 0, 0);
			//taloni.configClosedLoopPeakOutput(1.0);
			//taloni.configClosedLoopPeriod(1);
			//taloni.configAuxPIDPolarity(false);
			taloni.configMotionCruiseVelocity(0, 0);
			taloni.configMotionAcceleration(0, 0);
			//taloni.configMotionProfileTrajectoryPeriod(0);
			taloni.configSetCustomParam(0, 0, 0);
			taloni.configPeakCurrentLimit(0, 0);
			taloni.configContinuousCurrentLimit(0, 0);
		}
	}
 }
