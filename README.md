# Slot Machine GUI

![demo-gif](./demo/slot-machine-demo.gif)

## Project Overview 
This is a slot machine simulation with a simple Swing GUI. This simulation includes 3 reels, 3 stop buttons, and a start button. When the start button is clicked, the three reels start rotating simultaneously. In addition, each of these reels can be stopped separately by clicking its corresponding stop button - demonstrating multithreading behavior.

## Concepts Demonstrated
- Java GUI development using Swing
- Multithreading
- Event-driven programming
- Thread control
- Thread-safe UI updates (EDT handling)

## Multithreading & UI Safety
Each reel is controlled by a separate thread (`SlotThread`)

Since Swing is **not thread-safe** - see references, UI updates are handled using `SwingUtilities.invokeLater()`

This ensures: 
- Safe updates to UI components
- No race conditions
- Stable and responsive interface

## How to Run 
1. Clone the repository 
```
git clone https://github.com/AsmaaMesbah/Slot-Machine-GUI.git
```
2. Navigate to the Slot-Machine-GUI/src directory
```
cd "./Slot-Machine-GUI/src"
```
3. Compile the Java file
```
javac slotMachine.java
```
4. Run the compiled file
```
java slotMachine
```

Alternatively, you can compile and run using your IDE. 

## Project Structure 
```
Slot-Machine-GUI/
│
├── src/
│   └── slotMachine.java
│
├── demo/
│   └── slot-machine-demo.gif
│
└── README.md
```

## References
- [The Event Dispatch Thread Java](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html)
