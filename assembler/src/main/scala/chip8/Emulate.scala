package chip8

object Emulate extends App {
  //    val asm: List[Short] = Loader.read(Loader.IBMLogo)

  // error codes https://github.com/daniel5151/AC8E/blob/master/roms/bc_test.txt
  val asm: List[Short] = Loader.read(Loader.BC_Test)
//  val asm: List[Short] = Loader.read(Loader.IBMLogo)

  val ast: List[Chip8Compiler.Line] = Chip8Compiler.compile(asm)
  ast.zipWithIndex.foreach(println)

  Chip8Emulator.run(ast)

}
