/*
  SMAM-1 8 Bit ALU experiment.

  16 bit multiply using a pure 8 bit ALU.

*/
#include "stdio.h"

// Multiply two 8–bit numbers `m` and `n` (unsigned char)
// and return a 8–bit number (unsigned char) representing the hi byte of the result
unsigned char aluMultiply8bitH(unsigned char m, unsigned char n) {
    return ((m*n) >> 8) & 0xff;
}

// Multiply two 8–bit numbers `m` and `n` (unsigned char)
// and return a 8–bit number (unsigned char) representing the hi byte of the result
unsigned char aluMultiply8bitL(unsigned char m, unsigned char n) {
    return (m*n) & 0xff;
}
 
 
// Multiply 16–bit integers using an 8–bit ALU 
unsigned short multiply16bit(unsigned short m, unsigned short n)
{
    unsigned char mLow = (m & 0x00FF);      
    unsigned char mHigh = (m & 0xFF00) >> 8;
 
    unsigned char nLow = (n & 0x00FF);     
    unsigned char nHigh = (n & 0xFF00) >> 8;
 
    unsigned char l_mLow_nLow = aluMultiply8bitL(mLow, nLow); 
    unsigned char l_mHigh_nLow = aluMultiply8bitL(mHigh, nLow); 
    unsigned char l_mLow_nHigh = aluMultiply8bitL(mLow, nHigh); 
    unsigned char l_mHigh_nHigh = aluMultiply8bitL(mHigh, nHigh);

    unsigned char h_mLow_nLow = aluMultiply8bitH(mLow, nLow); 
    unsigned char h_mHigh_nLow = aluMultiply8bitH(mHigh, nLow);
    unsigned char h_mLow_nHigh = aluMultiply8bitH(mLow, nHigh);
    unsigned char h_mHigh_nHigh = aluMultiply8bitH(mHigh, nHigh);

    unsigned short sum = l_mLow_nLow + 
        ((l_mHigh_nLow + l_mLow_nHigh + h_mLow_nLow) << 8) + 
        ((l_mHigh_nHigh + h_mHigh_nLow + h_mLow_nHigh) << 16) + 
        (h_mHigh_nHigh << 24);

    return sum;
}
 
int main()
{
    // alu works even with signed values cast to unsigned
    if (multiply16bit(2,3) != 6) {
        printf("2,3 error\n");
    }
    if (multiply16bit(-2,3) != (0xffff)& -6) {
        printf("-2,3 error - got %x\n", 0xffff&multiply16bit(-2,3));
    }
    if (multiply16bit(-2,-3) != 6) {
        printf("-2,-3 error - got %x\n", 0xffff&multiply16bit(-2,-3));
    }

    // test using squares between min short and max short
    for (int j = -32768; j < 32768; j++) {
      unsigned short i = j;
      unsigned short expected = (i*i) & 0xffff;
      unsigned short actual = 0xffff&multiply16bit(i,i);

      if (expected != actual) {
        printf("expected %x != %x actual\n", expected, actual);
      }
    }
    
 
    return 0;
}
