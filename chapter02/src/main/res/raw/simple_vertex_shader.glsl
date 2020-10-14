// 对于定义过的每个单一的顶点，顶点着色器都会被调用一次；当它被调用的时候，它会在 a_Position 属性里会接收当前顶点的位置，
// 这个属性会被定义成 vec4 类型。
attribute vec4 a_Position;

void main()
{
    // 把定义过的位置复制到指定的输出变量 gl_Position
    gl_Position = a_Position;
}