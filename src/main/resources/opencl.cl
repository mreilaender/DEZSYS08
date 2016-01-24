__kernel void add_floats(__global const float* a, __global const float* b, __global float* out, int n)
{
    int i = get_global_id(0);
    if (i >= n)
        return;

    out[i] = a[i] + b[i];
}
__kernel void calculate_pi(__global const int* start, __global const int* end, __global float* out, int n)
{
    int i;
    printf("Start: %d, End: %d", start[0], end[0]);
    //for(i=start; i < *end)
}