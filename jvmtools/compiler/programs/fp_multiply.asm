
; ==================== MACROS ===========================

.macro fp_multiply_8_16_vc RESULT V1 CONST
  ; C = multiply 8 bit var by 16 bit fixed point VAR with overflow into FP_R (scrambled)
  ; FP_R scrambled cos .. $FFFE*21=14FFD but this is overflowed garbage as 14 would imply a sign change - sign extended value into 12 bits should have been FFFFD6
  ; So FP_R if useful should only be an equality check
  ; 8_16 special function avoids need to shift >> 8
  ; TRASHES REGC
   REGD             = [:V1]
   REGC             = REGD *LO >CONST
   [ :RESULT + 0 ]  = REGC

   REGC             = REGD *HI >CONST
   [ :RESULT + 1 ]  = REGC
   
   REGC             = REGD *LO <CONST
   REGC             = REGC A_PLUS_B [ :RESULT + 1 ]
   [ :RESULT + 1 ]  = REGC

   ; NOT NEEDED ....
   ;REGC             = REGD *HI <CONST
   ;[ :FP_R + 0 ]    = REGC
   ;[ :FP_R + 1 ]    = 0

.endmacro


.macro fp_multiply_8_16_vv RESULT V1 V2
  ; C = multiply 8 bit var by 16 bit fixed point VAR with overflow into FP_R (scrambled)
  ; FP_R scrambled cos .. $FFFE*21=14FFD but this is overflowed garbage as 14 would imply a sign change - sign extended value into 12 bits should have been FFFFD6
  ; So FP_R if useful should only be an equality check
  ; 8_16 special function avoids need to shift >> 8
  ; TRASHES REGC
   REGD             = [:V1]
   REGC             = REGD *LO [ :V2 + 0 ]
   [ :RESULT + 0 ]  = REGC

   REGC             = REGD *HI [ :V2 + 0 ]
   [ :RESULT + 1 ]  = REGC
   
   REGC             = REGD *LO [ :V2 + 1 ]
   REGC             = REGC A_PLUS_B [ :RESULT + 1 ]
   [ :RESULT + 1 ]  = REGC

   ; NOT NEEDED ....
   ;REGC             = REGD *HI [ :V2 + 1 ]
   ;[ :FP_R + 0 ]    = REGC
   ;[ :FP_R + 1 ]    = 0

.endmacro


.macro fp_multiply_16_16_vv RESULT V1 V2 
  ; Trashes REGC and REGD
__LOCAL___sign: BYTES [ 0 ]
__LOCAL___v1: WORD $0000
__LOCAL___v2: WORD $0000

   ; for 16 bit 8fp8 x 8fp8 we need to deal with postive numbers and deal with sign properly
   [:__LOCAL___sign] = 0

   REGC = [:V1]
   [:__LOCAL___v1]   = REGC

   REGC = [:V1+1]
   [:__LOCAL___v1+1] = REGC

   REGC = [:V2]
   [:__LOCAL___v2]   = REGC

   REGC = [:V2+1]
   [:__LOCAL___v2+1] = REGC

   
__LOCAL___v1_sign:
   NOOP = [:V1+1] _S
   PCHITMP = <:__LOCAL___v2_sign
   PC      = >:__LOCAL___v2_sign ! _N

   ; save the sign flag
   [:__LOCAL___sign] = $80

   ; flip sign
   REGC = [:V1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:__LOCAL___v1] = REGC 

  
   REGC = [:V1+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:__LOCAL___v1+1] = REGC 


__LOCAL___v2_sign:
   NOOP = [:V2+1] _S
   PCHITMP = <:__LOCAL__exec
   PC      = >:__LOCAL__exec ! _N

   ; save the sign flag
   REGC = [:__LOCAL___sign]
   REGC = REGC + $80
   [:__LOCAL___sign] = REGC

   ; flip sign
   REGC = [:V2]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:__LOCAL___v2] = REGC
  
   REGC = [:V2+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:__LOCAL___v2+1] = REGC 

__LOCAL__exec:

   ; === calc low result & carry ===
   ; REGC = calc two terms of lo result
   REGC             = [:__LOCAL___v1+1]
   REGC             = REGC *LO [:__LOCAL___v2+0]
   REGD             = [:__LOCAL___v1+0]
   REGD             = REGD *LO [:__LOCAL___v2+1]
   REGC             = REGC + REGD _S

   ; stash the carry in the upper byte result
   [:RESULT+1]      = 0
   [:RESULT+1]      = 1 _C

   ; calc 3rd term of low result
   REGD             = [:__LOCAL___v1+0]
   REGD             = REGD *HI [:__LOCAL___v2+0]
   REGC             = REGC + REGD _S

   ; save low byte
   [:RESULT+0]      = REGC

   ; update the existing carry if needed
   REGD             = [:RESULT+1]
   REGD             = REGD A_PLUS_B_PLUS_C 0
   [:RESULT+1]      = REGD

   ; === calc high result ===
   ; REGC still contains running hi result
   REGC             = [:__LOCAL___v1+1]
   REGC             = REGC *HI [:__LOCAL___v2+0]
   REGD             = [:__LOCAL___v1+0]
   REGD             = REGD *HI [:__LOCAL___v2+1]
   REGC             = REGC + REGD 

   ; add carry from low back in to running hi byte value
   REGC             = REGC A_PLUS_B [:RESULT+1]

   ; calc 3rd term of hi result
   REGD             = [:__LOCAL___v1+1]
   REGD             = REGD *LO [:__LOCAL___v2+1]
   REGC             = REGC + REGD

   ; save hi byte
   [:RESULT+1]      = REGC

;   PCHITMP = <:__LOCAL__end
;   PC      = >:__LOCAL__end 

;MARLO=[:RESULT]
;MARHI=[:RESULT+1]
;HALT=[:__LOCAL__sign]

   ; check if need to fix sign
   NOOP = [:__LOCAL___sign] _S
   PCHITMP = <:__LOCAL__end
   PC      = >:__LOCAL__end ! _N

   ; flip sign
   REGC = [:RESULT]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:RESULT] = REGC 
  

   REGC = [:RESULT+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:RESULT+1] = REGC 


__LOCAL__end:
   ; end of multiply

.endmacro

; ==================== MACROS ===========================
