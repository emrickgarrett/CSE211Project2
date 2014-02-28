package piano;

import javax.sound.midi.MidiUnavailableException;

import midi.*;
import music.Pitch;

public class PianoMachine {
	
	private Midi midi;
	Instrument instr = Instrument.PIANO;
	private int pitch;
    
	/**
	 * constructor for PianoMachine.
	 * 
	 * initialize midi device and any other state that we're storing.
	 */
    public PianoMachine() {
    	try {
            midi = Midi.getInstance();
        } catch (MidiUnavailableException e1) {
            System.err.println("Could not initialize midi device");
            e1.printStackTrace();
            return;
        }
    }
    
    /**
     * Method that begins a certain note
     * @param rawPitch : Frequency of the note to begin
     */
    public void beginNote(Pitch rawPitch) {
    	midi.beginNote(rawPitch.transpose(pitch).toMidiFrequency(), instr);
    	//TODO implement for question 1

    }
    
    /**
     * Method that ends the playing of a certain note
     * @param rawPitch : Frequency of the note to end
     */
    public void endNote(Pitch rawPitch) {
    	midi.endNote(rawPitch.transpose(pitch).toMidiFrequency(), instr);
    	//TODO implement for question 1
    }
    
    /**
     * Changes the current instrument being played to the next instrument in
     * the list
     */
    public void changeInstrument() {
      instr = instr.next();
       
    }
    
    /**
     * Shifts the notes up by one octave (12 semitones)
     */
    public void shiftUp() {
    	if(pitch <= 24) pitch+= 12;
    }
    
    /**
     * Shifts the notes down by one octave (12 semitones)
     */
    public void shiftDown() {
    	if(pitch >= -24) pitch -= 12;
    }
    
    //TODO write method spec
    public boolean toggleRecording() {
    	return false;
    	//TODO: implement for question 4
    }
    
    //TODO write method spec
    protected void playback() {    	
        //TODO: implement for question 4
    }

}
