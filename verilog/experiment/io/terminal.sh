
while read -e line
do
    echo -n $line > /tmp/fifo
done

