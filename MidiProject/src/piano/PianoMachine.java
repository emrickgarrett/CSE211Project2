package piano;

import java.util.ArrayList;

import javax.sound.midi.MidiUnavailableException;

import midi.*;
import music.NoteEvent;
import music.Pitch;

public class PianoMachine {
	
	private Midi midi;
	Instrument instr = Instrument.PIANO;
	private int pitch;
	private ArrayList<NoteEvent> track = new ArrayList<NoteEvent>();
	private boolean recording = false;
	private long trackEnd = 0;
    
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
    	if(recording)track.add(new NoteEvent(rawPitch.transpose(pitch), System.currentTimeMillis(), instr, NoteEvent.Kind.start));

    }
    
    /**
     * Method that ends the playing of a certain note
     * @param rawPitch : Frequency of the note to end
     */
    public void endNote(Pitch rawPitch) {
    	midi.endNote(rawPitch.transpose(pitch).toMidiFrequency(), instr);
    	if(recording)track.add(new NoteEvent(rawPitch.transpose(pitch), System.currentTimeMillis(), instr, NoteEvent.Kind.stop));
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
    	if(pitch < 24) pitch+= 12;
    }
    
    /**
     * Shifts the notes down by one octave (12 semitones)
     */
    public void shiftDown() {
    	if(pitch > -24) pitch -= 12;
    }
    
    /**
     * Toggles recording for the midi. If on, return false.
     * One it finishes recording, it returns true and can be playedback.
     * @return
     */
    public boolean toggleRecording() {
    	recording = !recording;
    	
    	
    	if(recording){
    		track = new ArrayList<NoteEvent>();
    	}else{
    		trackEnd = System.currentTimeMillis();
    	}
    	return recording;
    }
    
    /**
     * Plays the recording that was saved
     */
    protected void playback() {
    	
    	NoteEvent e = null;
    	final long startPlaybackTime = System.currentTimeMillis();
    	
    	for(int i = 0; i < track.size(); i++){
    		e = track.get(i);
    		
    		if(e.getKind() == NoteEvent.Kind.start){
    			midi.beginNote(e.getPitch().toMidiFrequency(), e.getInstr());
    			final int start = i;
    			final NoteEvent tmp = e;
    						
    			new Thread(){
    				
    				@Override
    				public void run(){
	    				boolean hasFound = false;
	    				NoteEvent event = tmp;
	    				long endTime = 0;
	    				long startTime = System.currentTimeMillis();
	    				
	    				while(!hasFound){
	    					for(int j = start; j < track.size(); j++){
	    						NoteEvent mTmp = track.get(j);
	    						if(mTmp.getKind() == NoteEvent.Kind.stop){
	    							if(mTmp.getInstr() == event.getInstr() && mTmp.getPitch() == event.getPitch()){
	    								hasFound = true;
	    								j = track.size();
	    								endTime = mTmp.getTime();
	    							}
	    						}
	    					}
	    					
	    					endTime = trackEnd;
	    					hasFound = true;
	    				}
	    				
	    				while(endTime - tmp.getTime() > System.currentTimeMillis() - startTime){
	    					//Delay to stop it from ending too soon.
	    				}
	    				
	    				midi.endNote(tmp.getPitch().toMidiFrequency(), tmp.getInstr());
	    				
    				}
    			}.start();
    			
    		}
    		/**
    		 * I don't think I need this . . .
    		 * else if(e.getKind() == NoteEvent.Kind.stop){
    			midi.endNote(e.getPitch().toMidiFrequency(), e.getInstr());	
    		}*/ 
    	}
    }

}
