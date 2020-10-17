

here=$(dirname $(readlink -f $0))

for file in $(find . -name 'old*' -prune -o -type f -name '*.v' -print)
do
    (
        echo ===============================
        echo =============================== $file
        dir=`dirname $file`
        cd $dir
        filename=`basename $file`
        #/home/john/wslapps/verilator/verilator/bin/verilator --sv --lint-only --language 1800-2012  +systemverilogext+v --relative-includes +1800-2012ext+v $filename
        $here/validate.sh $filename
        if [ $? -ne 0 ]
        then
            echo failed validation : $dir/$filename
            exit 1
        fi
    )
    if [ $? -ne 0 ]
    then
        echo failed validation
        exit 1
    fi
done

