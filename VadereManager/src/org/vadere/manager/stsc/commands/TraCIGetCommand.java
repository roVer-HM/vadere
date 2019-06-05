package org.vadere.manager.stsc.commands;

import org.vadere.manager.TraCIException;
import org.vadere.manager.stsc.TraCICommandBuffer;

import java.util.EnumSet;

public class TraCIGetCommand extends TraCICommand {

	protected int variableId;
	protected String elementId;

	public TraCIGetCommand(TraCICmd traCICmd, TraCICommandBuffer cmdBuffer) {
		super(traCICmd);
		EnumSet<TraCICmd> set;
		try{
			variableId = cmdBuffer.reader.readUnsignedByte();
			elementId = cmdBuffer.reader.readString();
		} catch (Exception e){
			throw TraCIException.cmdErr(traCICmd, e);
		}
	}


	public int getVariableId() {
		return variableId;
	}

	public String getElementId() {
		return elementId;
	}


}