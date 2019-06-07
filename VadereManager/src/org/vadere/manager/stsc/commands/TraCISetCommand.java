package org.vadere.manager.stsc.commands;

import org.vadere.manager.TraCIException;
import org.vadere.manager.stsc.TraCICmd;
import org.vadere.manager.stsc.TraCIPacket;
import org.vadere.manager.stsc.reader.TraCICommandBuffer;
import org.vadere.manager.stsc.TraCIDataType;

public class TraCISetCommand extends TraCICommand{

	protected int variableId;
	protected String elementId;
	protected TraCIDataType returnDataType;
	protected Object variableValue;


	public TraCISetCommand(TraCICmd traCICmd, TraCICommandBuffer cmdBuffer) {
		super(traCICmd);
		try{
			variableId = cmdBuffer.reader.readUnsignedByte();
			elementId = cmdBuffer.reader.readString();
			returnDataType = TraCIDataType.fromId(cmdBuffer.reader.readUnsignedByte());
			variableValue = cmdBuffer.reader.readTypeValue(returnDataType);
		} catch (Exception e){
			throw TraCIException.cmdErr(traCICmd, e);
		}
	}

	public Object getVariableValue(){
		return variableValue;
	}

	public int getVariableId() {
		return variableId;
	}

	public String getElementId() {
		return elementId;
	}

	public TraCIDataType getReturnDataType() {
		return returnDataType;
	}

	@Override
	public TraCIPacket buildResponsePacket() {
		return null;
	}
}
