
; ==================== MACROS ===========================

; C = multiply 8 bit register by fixed point VAR with overflow into FP_R (scrambled)
;
; FP_R scrambled cos .. $FFFE*21=14FFD but this is overflowed garbage as 14 would imply a sign change - sign extended value into 12 bits should have been FFFFD6
; So FP_R if useful should only be an equality check
; 8_16 special function avoids need to shift >> 8
; TRASHES REGC
.macro fp_multiply_8_16 RESULT REGX VAR
   REGC             = REGX *LO [ :VAR + 0 ]
   [ :RESULT + 0 ]  = REGC

   REGC             = REGX *HI [ :VAR + 0 ]
   [ :RESULT + 1 ]  = REGC
   
   REGC             = REGX *LO [ :VAR + 1 ]
   REGC             = REGC A_PLUS_B [ :RESULT + 1 ]
   [ :RESULT + 1 ]  = REGC

   ; NOT NEEDED ....
   REGC             = REGX *HI [ :VAR + 1 ]
   [ :FP_R + 0 ]    = REGC
   [ :FP_R + 1 ]    = 0

.endmacro

; Trashes REGC and REGD
fp_multiply_16_16_sign: BYTES [ 0 ]
fp_multiply_16_16_v1: WORD $0000
fp_multiply_16_16_v2: WORD $0000

.macro fp_multiply_16_16 RESULT V1 V2 

   ; for 16 bit 8fp8 x 8fp8 we need to deal with postive numbers and deal with sign properly
   [:__NAME___sign] = 0

   REGC = [:V1]
   [:__NAME___v1]   = REGC

   REGC = [:V1+1]
   [:__NAME___v1+1] = REGC

   REGC = [:V2]
   [:__NAME___v2]   = REGC

   REGC = [:V2+1]
   [:__NAME___v2+1] = REGC

   
__NAME___v1_sign___#__:
   NOOP = [:V1+1] _S
   PCHITMP = <:__NAME___v2_sign___#__
   PC      = >:__NAME___v2_sign___#__ ! _N

   ; save the sign flag
   [:__NAME___sign] = $80

   ; flip sign
   REGC = [:V1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:__NAME___v1] = REGC 

  
   REGC = [:V1+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:__NAME___v1+1] = REGC 


__NAME___v2_sign___#__:
   NOOP = [:V2+1] _S
   PCHITMP = <:__NAME__exec___#__
   PC      = >:__NAME__exec___#__ ! _N

   ; save the sign flag
   REGC = [:__NAME___sign]
   REGC = REGC + $80
   [:__NAME___sign] = REGC

   ; flip sign
   REGC = [:V2]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:__NAME___v2] = REGC
  
   REGC = [:V2+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:__NAME___v2+1] = REGC 

__NAME__exec___#__:

   ; === calc low result & carry ===
   ; REGC = calc two terms of lo result
   REGC             = [:__NAME___v1+1]
   REGC             = REGC *LO [:__NAME___v2+0]
   REGD             = [:__NAME___v1+0]
   REGD             = REGD *LO [:__NAME___v2+1]
   REGC             = REGC + REGD _S

   ; stash the carry in the upper byte result
   [:RESULT+1]      = 0
   [:RESULT+1]      = 1 _C

   ; calc 3rd term of low result
   REGD             = [:__NAME___v1+0]
   REGD             = REGD *HI [:__NAME___v2+0]
   REGC             = REGC + REGD _S

   ; save low byte
   [:RESULT+0]      = REGC

   ; update the existing carry if needed
   REGD             = [:RESULT+1]
   REGD             = REGD A_PLUS_B_PLUS_C 0
   [:RESULT+1]      = REGD

   ; === calc high result ===
   ; REGC still contains running hi result
   REGC             = [:__NAME___v1+1]
   REGC             = REGC *HI [:__NAME___v2+0]
   REGD             = [:__NAME___v1+0]
   REGD             = REGD *HI [:__NAME___v2+1]
   REGC             = REGC + REGD 

   ; add carry from low back in to running hi byte value
   REGC             = REGC A_PLUS_B [:RESULT+1]

   ; calc 3rd term of hi result
   REGD             = [:__NAME___v1+1]
   REGD             = REGD *LO [:__NAME___v2+1]
   REGC             = REGC + REGD

   ; save hi byte
   [:RESULT+1]      = REGC

;   PCHITMP = <:__NAME__end___#__
;   PC      = >:__NAME__end___#__ 

;MARLO=[:RESULT]
;MARHI=[:RESULT+1]
;HALT=[:__NAME__sign]

   ; check if need to fix sign
   NOOP = [:__NAME___sign] _S
   PCHITMP = <:__NAME__end___#__
   PC      = >:__NAME__end___#__ ! _N

   ; flip sign
   REGC = [:RESULT]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B 1 _S
   [:RESULT] = REGC 
  

   REGC = [:RESULT+1]
   REGC = REGC ^ $FF
   REGC = REGC A_PLUS_B_PLUS_C 0 
   [:RESULT+1] = REGC 


__NAME__end___#__:
   ; end of multiply

.endmacro

