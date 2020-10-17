
here=$(dirname $(readlink -f $0))

set -e
for file in $(find . -name 'old*' -prune -o -type f -name 'test*.v' -print)
do
    (
    echo checking $file
    dir=`dirname $file`
    cd $dir
    filename=`basename $file`
    $here/simulate.sh $filename
        if [ $? -ne 0 ]
        then
            echo failed simulation : $dir/$filename
            exit 1
        fi
    )
    if [ $? -ne 0 ]
    then
        echo failed simulation : $dir/$filename
        exit 1
    fi
done

