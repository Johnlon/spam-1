; https://youtu.be/BWQDAKTLFXk?t=815

; needed for verilog sim
MARLO=0
MARHI=0
REGA=0
REGB=0
REGC=0
REGD=0

; =============================

MAND_WIDTH: EQU 32
MAND_HEIGHT: EQU 22

REGA = 0 ; x
REGB = 0 ; y

putic '\n' 
putic '\n' 
putic '\n' 
putic '\n' 

loop:
  ; print x and y as hex string
  putc '\n' 
  putc 'x' 
  putc '=' 
  putc '$'
  puth REGA
  putc 32
  putc 'y' 
  putc '=' 
  putc '$'
  puth REGB
  putc 32

  ; Mand iters value ends up in :mand_value
  JMP mand_get
mand_get_ret:

  ; print iters as a mapped colourised value
  REGC=[:mand_value]
  putiA REGC 

  ; print iters as a log string
  putc 32
  putc '#' 
  putc 'i' 
  putc '=' 
  putc '$'
  puth REGC
  putc '\n'

next_x:
  ; inc X - and break if at X limit
  REGA = REGA + 1

  ; if x = MAND_WIDTH then echo \n
  NOOP = REGA A_MINUS_B :MAND_WIDTH _S
  JMP_NOT next_loop _Z 
  
  putic '\n'
  REGA = 0

next_y:
  ; inc Y
  REGB = REGB + 1

  ; if y = MAND_HEIGHT then exit
  NOOP = REGB A_MINUS_B :MAND_HEIGHT _S
  JMP_NOT next_loop _Z 
  JMP prog_end

next_loop:
  JMP loop

prog_end:
  putic '\n' 
  putic '\n' 
  putic '\n' 
  putic '\n' 

  HALT = 0


.include mand_get.asm
.include mand_macros.asm
.include division.asm
.include fp_multiply.asm


END
