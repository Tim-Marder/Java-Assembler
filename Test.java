package project;

public class Test {

	public static void main(String[] args) {

		Instruction instr2 = new Instruction((byte) 15, 0);
		int ones2 = Instruction.numOnes(instr2.opcode);
		Instruction.checkParity(instr2);
		System.out.println(ones2);
		
		//Instruction instr = new Instruction((byte) 14, 0);
		//int ones = Instruction.numOnes(instr.opcode);
		//Instruction.checkParity(instr);
		//System.out.println(ones);

	}

}
