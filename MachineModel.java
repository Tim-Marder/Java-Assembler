package project;

import static project.Instruction.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class MachineModel {
	
	public final Map<Integer, Consumer<Instruction>> ACTION = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callBack;
	
	public MachineModel(HaltCallback callBack) {
		this.callBack = callBack;
		
		ACTION.put(OPCODES.get("NOP"), instr -> {
			int flags = instr.opcode & 6;
			if (flags != 0) {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("NOT"), instr -> {
			int flags = instr.opcode & 6;
			if (flags != 0) {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			if (cpu.accum == 0) cpu.accum = 1;
			else cpu.accum = 0;
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("HALT"), instr -> {
			int flags = instr.opcode & 6;
			if (flags != 0) {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			halt();
		});
		ACTION.put(OPCODES.get("LOD"), instr -> {
			int flags = instr.opcode & 6;
			if (flags == 0) cpu.accum = memory.getData(instr.arg);
			else if (flags == 2) cpu.accum = instr.arg;
			else if (flags == 4) cpu.accum = memory.getData(memory.getData(instr.arg));
			else {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("STO"), instr -> {
			int flags = instr.opcode & 6;
			if (flags == 0) setData(instr.arg, cpu.accum);
			else if (flags == 4) setData(memory.getData(instr.arg),cpu.accum);
			else {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("ADD"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if (flags == 0) cpu.accum += memory.getData(instr.arg);
			else if (flags == 2) cpu.accum += instr.arg;
			else if (flags == 4) cpu.accum += memory.getData(memory.getData(instr.arg));
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			 }
			 cpu.pc++;
		});
		ACTION.put(OPCODES.get("SUB"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if (flags == 0) cpu.accum -= memory.getData(instr.arg);
			else if (flags == 2) cpu.accum -= instr.arg;
			else if (flags == 4) cpu.accum -= memory.getData(memory.getData(instr.arg));
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			 }
			 cpu.pc++;
		});
		ACTION.put(OPCODES.get("MUL"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if (flags == 0) cpu.accum *= memory.getData(instr.arg);
			else if (flags == 2) cpu.accum *= instr.arg;
			else if (flags == 4) cpu.accum *= memory.getData(memory.getData(instr.arg));
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			 }
			 cpu.pc++;
		});
		ACTION.put(OPCODES.get("DIV"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if (flags == 0) {
				if (memory.getData(instr.arg) == 0) throw new DivideByZeroException("Cannot divide by zero.");
				else cpu.accum /= memory.getData(instr.arg);
			}
			else if (flags == 2) {
				if (instr.arg == 0) throw new DivideByZeroException("Cannot divide by zero.");
				else cpu.accum /= instr.arg;
			}
			else if (flags == 4) {
				if (memory.getData(memory.getData(instr.arg)) == 0) throw new DivideByZeroException("Cannot divide by zero.");
				else cpu.accum /= memory.getData(memory.getData(instr.arg));
			}
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			 }
			 cpu.pc++;
		});
		ACTION.put(OPCODES.get("AND"), instr -> {
			int flags = instr.opcode & 6;
			if (flags == 0) {
				if (cpu.accum != 0 && memory.getData(instr.arg) != 0) cpu.accum = 1;
				else cpu.accum = 0;
			}
			else if (flags == 2) {
				if (cpu.accum != 0 && instr.arg != 0) cpu.accum = 1;
				else cpu.accum = 0;
			}
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("JUMP"), instr -> {
			int flags = instr.opcode & 6;
			if (flags == 0) cpu.pc += instr.arg;
			else if (flags == 2) cpu.pc = instr.arg;
			else if (flags == 4) cpu.pc += memory.getData(instr.arg);
			else cpu.pc = memory.getData(instr.arg);
		});
		ACTION.put(OPCODES.get("JMPZ"), instr -> {
			if (cpu.accum == 0) {
				int flags = instr.opcode & 6;
				if (flags == 0) cpu.pc += instr.arg;
				else if (flags == 2) cpu.pc = instr.arg;
				else if (flags == 4) cpu.pc += memory.getData(instr.arg);
				else cpu.pc = memory.getData(instr.arg);
			}
			else cpu.pc++;
		});
		ACTION.put(OPCODES.get("CMPL"), instr -> {
			int flags = instr.opcode & 6;
			if (flags != 0) {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			if (memory.getData(instr.arg) < 0) cpu.accum = 1;
			else cpu.accum = 0;
			cpu.pc++;
		});
		ACTION.put(OPCODES.get("CMPZ"), instr -> {
			int flags = instr.opcode & 6;
			if (flags != 0) {
				String fString =  "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			if (memory.getData(instr.arg) == 0) cpu.accum = 1;
			else cpu.accum = 0;
			cpu.pc++;
		});
		
	}//end of constructor

	private class CPU {
		
		private int accum; //accumulator
		private int pc; //program counter
		
	}
	
	public int[] getData(int min, int max) {
		return memory.getData(min, max);
	}
		 
	public int[] getData() {
		return memory.getData();
	}

	public int getData(int index) {
		return memory.getData(index);
	}
	
	public void setData(int i, int j) {
		memory.setData(i, j);
	}
	public int getPC() {
		return cpu.pc;
	}
	public int getAccum() {
		return cpu.accum;
	}
	public void setAccum(int i) {
		cpu.accum = i;
	}
	public void setPC(int i) {
		cpu.pc = i;
	}
	
	public Instruction getCode(int index) {
		return memory.getCode(index);
	}
	
	public void setCode(int i, Instruction j) {
		memory.setCode(i, j);
	}
	
	public Instruction[] getCode() {
		return memory.getCode();
	}
	
	public Instruction[] getCode(int min, int max) {
		return memory.getCode(min, max);
	}
	
	public int getProgramSize() {
		return memory.getProgramSize();
	}
	
	public int getChangedDataIndex() {
		return memory.getChangedDataIndex();
	}
	
	public void setProgramSize(int i) {
		memory.setProgramSize(i);
	}

	public void halt() {
		callBack.halt();
	}
	
	public void clear() {
		memory.clearData();
		memory.clearCode();
		cpu.accum = 0;
		cpu.pc = 0;
	}
	
	public void step() {
		try {
			Instruction instr = getCode(cpu.pc);
			Instruction.checkParity(instr);
			ACTION.get(instr.opcode/8).accept(instr);
		}
		catch(Exception e) {
			halt();
			throw e;
		}
	}
	
}

