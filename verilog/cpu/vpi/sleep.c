# include  <vpi_user.h>
# include  <time.h>

static int sleep_compiletf(char* user_data)
{
    return 0;
}

static int sleep_calltf(char* user_data)
{
    //      vpi_printf("Hello, World! %s\n", user_data);
    // https://groups.google.com/g/comp.lang.verilog/c/yR769Bzakt4

    vpiHandle systfref, args_iter, argh;
    struct t_vpi_value argval;
    int value;

    systfref = vpi_handle(vpiSysTfCall, NULL);
    args_iter = vpi_iterate(vpiArgument, systfref);

    argh = vpi_scan(args_iter);
    argval.format = vpiIntVal;
    vpi_get_value(argh, &argval);
    value = argval.value.integer;
//    vpi_printf("VPI routine received %d\n", value);


    struct timespec ts_sleep = 
    {
        value / 1000,
        (value % 1000) * 1000000L
    };
    nanosleep(&ts_sleep, NULL);

    return 0;
}

void sleep_register()
{
    s_vpi_systf_data tf_data;

    tf_data.type      = vpiSysTask;
    tf_data.tfname    = "$sleep";
    tf_data.calltf    = sleep_calltf;
    tf_data.compiletf = sleep_compiletf;
    tf_data.sizetf    = 0;
    tf_data.user_data = 0;
    vpi_register_systf(&tf_data);
}

void (*vlog_startup_routines[])() = {
    sleep_register,
    0
};

