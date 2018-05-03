package datastructures;


import java.util.Arrays;

// StackAr class
//
// CONSTRUCTION: with or without a capacity; default is 10
//
// ******************PUBLIC OPERATIONS*********************
// void push( x )        --> Insert x
// void pop()            --> Remove most recently inserted item
// Object top()          --> Return most recently inserted item
// Object topAndPop()    --> Return and remove most recently inserted item
// boolean isEmpty()     --> Return true if empty; else false
// boolean isFull()      --> Return true if full; else false
// void makeEmpty()      --> Remove all items
// ******************ERRORS********************************
// Overflow and Underflow thrown as needed

/**
 * Array-based implementation of the stack.
 * @author Mark Allen Weiss
 */
public class StackAr
{
	private Object[] theArray;
	private int topOfStack;
	static final int DEFAULT_CAPACITY = 10;
	   
    /**
     * Construct the stack.
     */
    public StackAr()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Construct the stack.
     * @param capacity the capacity.
     */
    public StackAr(int capacity)
    {
        theArray = new Object[capacity];
        topOfStack = -1;
    }

    /**
     * Test if the stack is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty()
    {
        boolean result = topOfStack == -1;        
        return result;
    }

    /**
     * Test if the stack is logically full.
     * @return true if full, false otherwise.
     */
    public boolean isFull()
    {
        boolean result = topOfStack == theArray.length - 1;
        return result;
    }

    /**
     * Make the stack logically empty.
     */
    public void makeEmpty()
    {
        java.util.Arrays.fill(theArray, 0, topOfStack + 1, null);
        topOfStack = -1;
    }

    /**
     * Get the most recently inserted item in the stack.
     * Does not alter the stack.
     * @return the most recently inserted item in the stack, or null, if empty.
     */
    public Object top()
    {
    	//instrumentation
    	int old_topOfStack = topOfStack;
    	//instrumentation
    	Object[] old_theArray = Arrays.copyOf(theArray, theArray.length);
    	
    	Object result;
        if (isEmpty()) {
            result = null;
        } else {
        	result = theArray[topOfStack];
        }

        //Assertion 1 (has False Positive)
        assert ((topOfStack == -1) == (result == null));
        
        return result;
    }
    
    /**
     * Remove the most recently inserted item from the stack.
     * @exception UnderflowException if stack is already empty.
     */
    public void pop() throws UnderflowException {
        //instrumentation
    	int old_topOfStack = topOfStack;
    	//instrumentation
    	Object[] old_theArray = Arrays.copyOf(theArray, theArray.length);
    	
    	if (isEmpty())
            throw new UnderflowException();
    	
        theArray[topOfStack] = null;
        topOfStack = topOfStack - 1; 

        //Assertion 2 (has False Negative)
        assert (theArray[topOfStack + 1] == null);
    } 
    

    /**
     * Insert a new item into the stack, if not already full.
     * @param x the item to insert.
     * @exception OverflowException if stack is already full.
     */
    public void push(Object x) throws OverflowException
    {
        if (isFull())
            throw new OverflowException();
       
        theArray[++topOfStack] = x;
    }
    
}