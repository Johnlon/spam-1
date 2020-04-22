

(while read line; do echo $line;  done < /tmp/fifo.rx)&

while read line; do echo $line >> /tmp/fifo.tx;  done 
