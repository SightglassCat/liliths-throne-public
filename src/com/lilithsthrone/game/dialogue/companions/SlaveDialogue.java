package com.lilithsthrone.game.dialogue.companions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lilithsthrone.game.PropertyValue;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.attributes.AffectionLevelBasic;
import com.lilithsthrone.game.character.attributes.ObedienceLevelBasic;
import com.lilithsthrone.game.character.body.CoverableArea;
import com.lilithsthrone.game.character.effects.Perk;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.npc.NPCFlagValue;
import com.lilithsthrone.game.character.npc.dominion.Helena;
import com.lilithsthrone.game.character.npc.dominion.Scarlett;
import com.lilithsthrone.game.character.npc.misc.NPCOffspring;
import com.lilithsthrone.game.character.quests.Quest;
import com.lilithsthrone.game.character.quests.QuestLine;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNode;
import com.lilithsthrone.game.dialogue.DialogueNodeType;
import com.lilithsthrone.game.dialogue.places.dominion.slaverAlley.ScarlettsShop;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.responses.ResponseEffectsOnly;
import com.lilithsthrone.game.dialogue.responses.ResponseSex;
import com.lilithsthrone.game.dialogue.responses.ResponseTag;
import com.lilithsthrone.game.dialogue.utils.CharactersPresentDialogue;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.clothing.ClothingType;
import com.lilithsthrone.game.inventory.item.ItemType;
import com.lilithsthrone.game.occupantManagement.slave.SlaveJob;
import com.lilithsthrone.game.occupantManagement.slave.SlaveJobSetting;
import com.lilithsthrone.game.occupantManagement.slave.SlavePermissionSetting;
import com.lilithsthrone.game.sex.ImmobilisationType;
import com.lilithsthrone.game.sex.SexControl;
import com.lilithsthrone.game.sex.SexPace;
import com.lilithsthrone.game.sex.managers.dominion.SMMilkingStall;
import com.lilithsthrone.game.sex.managers.universal.SMGeneric;
import com.lilithsthrone.game.sex.positions.slots.SexSlotMilkingStall;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.AbstractPlaceUpgrade;
import com.lilithsthrone.world.places.PlaceType;
import com.lilithsthrone.world.places.PlaceUpgrade;

/**
 * @since 0.1.85
 * @version 0.3.9.2
 * @author Innoxia
 */
public class SlaveDialogue {

	private static NPC slave;
	private static NPC characterForSex;
	private static NPC characterForSexSecondary;
	
	private static List<NPC> charactersPresent;
	
	private static boolean initFromCharactersPresent;
	private static boolean dollStatueInterrupted;
	
	public static void initDialogue(NPC targetedSlave, boolean initFromCharactersPresent) {
		CompanionManagement.initManagement(SLAVE_START, 2, targetedSlave);
		slave = targetedSlave;
		characterForSex = targetedSlave;

		characterForSexSecondary = null;
		charactersPresent = new ArrayList<>(Main.game.getCharactersPresent());
		charactersPresent.removeIf((npc) -> !Main.game.getPlayer().getCompanions().contains(npc) && (!npc.isSlave() || !npc.getOwner().isPlayer()) && !Main.game.getPlayer().getFriendlyOccupants().contains(npc.getId()));
		if(charactersPresent.size()>1) {
			if(charactersPresent.contains(Main.game.getPlayer().getMainCompanion()) && !getSlave().equals(Main.game.getPlayer().getMainCompanion())) {
				characterForSexSecondary = (NPC) Main.game.getPlayer().getMainCompanion();
				
			} else {
				characterForSexSecondary = charactersPresent.stream().filter((npc) -> !npc.equals(getSlave())).findFirst().get();
			}
		}
		
		SlaveDialogue.initFromCharactersPresent = initFromCharactersPresent;
		dollStatueInterrupted = false;
	}
	
	private static DialogueNode getAfterSexDialogue() {
		if(initFromCharactersPresent) {
			return CharactersPresentDialogue.AFTER_SEX;
		} else {
			return AFTER_SEX;
		}
	}
	
	private static NPC getSlave() {
		return slave;
	}
	
	private static boolean isDoll() {
		return getSlave().isDoll();
	}
	
	private static boolean isDollStatue() {
		return getSlave().isDollStatue();
	}
	
	private static ResponseTag getDollSexTag() {
		if(isDollStatue()) {
			if(getSlave().hasSlaveJobSetting(SlaveJob.DOLL_STATUE, SlaveJobSetting.DOLL_STATUE_ALL_FOURS)) {
				return ResponseTag.PREFER_DOGGY;
				
			} else if(getSlave().hasSlaveJobSetting(SlaveJob.DOLL_STATUE, SlaveJobSetting.DOLL_STATUE_MISSIONARY) || getSlave().hasSlaveJobSetting(SlaveJob.DOLL_STATUE, SlaveJobSetting.DOLL_STATUE_BRIDGE)) {
				return ResponseTag.PREFER_MISSIONARY;
				
			} else if(getSlave().hasSlaveJobSetting(SlaveJob.DOLL_STATUE, SlaveJobSetting.DOLL_STATUE_SQUATTING)) {
				return ResponseTag.PREFER_ORAL;
			}
		}
		return null;
	}
	
	public static boolean isDollStatueInterrupted() {
		return dollStatueInterrupted;
	}
	
	private static SlaveJob getCurrentJob() {
		return getSlave().getSlaveJob(Main.game.getHourOfDay());
	}

	public static String getTextFilePath() {
		if(getSlave().isRelatedTo(Main.game.getPlayer())) {
			return "characters/offspring/slave";
		} else {
			return "misc/slaveDialogue";
		}
	}

	private static String getThreesomeTextFilePath() {
		if(characterForSex.isRelatedTo(Main.game.getPlayer()) || (characterForSexSecondary!=null && characterForSexSecondary.isRelatedTo(Main.game.getPlayer()))) {
			return "characters/offspring/slave";
		} else {
			return "misc/slaveDialogue";
		}
	}
	
	private static void applyReactionReset() {
		if(getSlave().isVisiblyPregnant()){
			getSlave().setCharacterReactedToPregnancy(Main.game.getPlayer(), true);
		}
		if(Main.game.getPlayer().isVisiblyPregnant()) {
			Main.game.getPlayer().setCharacterReactedToPregnancy(getSlave(), true);
		}
	}

	private static boolean isCompanionSexPublic() {
		return Main.game.getPlayer().getLocationPlace().isPopulated()
				&& !Main.game.getPlayer().getLocationPlace().getPlaceType().equals(PlaceType.WATERING_HOLE_SEATING_AREA)
				&& !Main.game.getPlayer().getLocationPlace().getPlaceType().equals(PlaceType.WATERING_HOLE_TOILETS);
	}
	
	private static GameCharacter enslavementTarget;
	private static DialogueNode followupEnslavementDialogue;

	public static GameCharacter getEnslavementTarget() {
		return enslavementTarget;
	}

	public static void setEnslavementTarget(GameCharacter enslavementTarget) {
		SlaveDialogue.enslavementTarget = enslavementTarget;
	}
	
	public static DialogueNode getFollowupEnslavementDialogue() {
		return followupEnslavementDialogue;
	}

	public static void setFollowupEnslavementDialogue(DialogueNode followupEnslavementDialogue) {
		SlaveDialogue.followupEnslavementDialogue = followupEnslavementDialogue;
	}

	private static SMGeneric getGenericSlaveSexManager(
			List<GameCharacter> dominantParticipants,
			List<GameCharacter> submissiveParticipants,
			List<ResponseTag> tags,
			boolean forcePlayerFullControl) {
		return getGenericSlaveSexManager(dominantParticipants,
						submissiveParticipants,
						getDominantSpectators(),
						getSubmissiveSpectators(),
						tags,
						forcePlayerFullControl,
						new HashMap<>());
	}
	
	private static SMGeneric getGenericSlaveSexManager(
			List<GameCharacter> dominantParticipants,
			List<GameCharacter> submissiveParticipants,
			List<GameCharacter> dominantSpectators,
			List<GameCharacter> submissiveSpectators,
			List<ResponseTag> tags,
			boolean forcePlayerFullControl,
			Map<GameCharacter, SexPace> startingSexPaces) {
		return new SMGeneric(
				dominantParticipants,
				submissiveParticipants,
				dominantSpectators,
				submissiveSpectators,
				tags) {
			@Override
			public SexPace getStartingSexPaceModifier(GameCharacter character) {
				if(startingSexPaces!=null && startingSexPaces.containsKey(character)) {
					return startingSexPaces.get(character);
				}
				return super.getStartingSexPaceModifier(character);
			}
			@Override
			public boolean isPublicSex() {
				return isCompanionSexPublic();
			}
			@Override
			public SexControl getSexControl(GameCharacter participant) {
				if(participant.isPlayer() && forcePlayerFullControl) {
					return SexControl.FULL;
				}
				return super.getSexControl(participant);
			}
			@Override
			public Map<ImmobilisationType, Map<GameCharacter, Set<GameCharacter>>> getStartingCharactersImmobilised() {
				ImmobilisationType immobilisationType = null;
				for(AbstractPlaceUpgrade upgrade : Main.game.getPlayer().getLocationPlace().getPlaceUpgrades()) {
					immobilisationType = upgrade.getImmobilisationType();
					if(immobilisationType!=null) {
						break;
					}
				}
				
				Map<ImmobilisationType, Map<GameCharacter, Set<GameCharacter>>> map = new HashMap<>();
				map.put(immobilisationType, new HashMap<>());
				map.get(immobilisationType).put(dominantParticipants.get(0), new HashSet<>());
				if(immobilisationType!=null) {
					for(GameCharacter character : submissiveParticipants) {
						if(character.isSlave() && !character.isDollStatue()) {
							map.get(immobilisationType).get(dominantParticipants.get(0)).add(character);
						}
					}
				}
				
				map.put(ImmobilisationType.COMMAND, new HashMap<>());
				map.get(ImmobilisationType.COMMAND).put(dominantParticipants.get(0), new HashSet<>());
				for(GameCharacter character : submissiveParticipants) {
					if(character.isDollStatue()) {
						map.get(ImmobilisationType.COMMAND).get(dominantParticipants.get(0)).add(character);
					}
				}

				map.put(ImmobilisationType.SLEEP, new HashMap<>());
				map.get(ImmobilisationType.SLEEP).put(dominantParticipants.get(0), new HashSet<>());
				for(GameCharacter character : submissiveParticipants) {
					if(character.isAsleep()) {
						map.get(ImmobilisationType.SLEEP).get(dominantParticipants.get(0)).add(character);
					}
				}
				
				return map;
			}
		};
	}
	
	//TODO delete -> was moved into Sex init in 0.4.9.5
//	private static void applyImmobilisationText(List<GameCharacter> submissiveParticipants) {
//		// Immobilisation from room upgrades:
//		ImmobilisationType immobilisationType = null;
//		for(AbstractPlaceUpgrade upgrade : Main.game.getPlayer().getLocationPlace().getPlaceUpgrades()) {
//			immobilisationType = upgrade.getImmobilisationType();
//			if(immobilisationType!=null) {
//				break;
//			}
//		}
//		if(immobilisationType!=null) {
//			List<GameCharacter> slaveSubs = new ArrayList<>(submissiveParticipants);
//			slaveSubs.removeIf(s->!s.isSlave() || s.isDollStatue());
//			if(!slaveSubs.isEmpty()) {
//				List<String> names = new ArrayList<>();
//				for(GameCharacter slave : slaveSubs) {
//					names.add(UtilText.parse(slave, "[npc.name]"));
//				}
//				StringBuilder sb = new StringBuilder();
//				sb.append("<p style='text-align:center;'>[style.italicsTerrible(");
//					switch(immobilisationType) {
//						case CHAINS:
//							sb.append("Thanks to the chains which have been added to this cell, ");
//							break;
//						case ROPE:
//							sb.append("Thanks to the ropes which have been added to this cell, ");
//							break;
//						case COCOON:
//						case TAIL_CONSTRICTION:
//						case TENTACLE_RESTRICTION:
//						case WITCH_SEAL:
//						case COMMAND:
//						case SLEEP:
//							break;
//					}
//					sb.append(Util.stringsToStringList(names, false));
//					if(names.size()>1) {
//						sb.append(" are");
//					} else {
//						sb.append(" is");
//					}
//					sb.append(" bound and unable to move!");
//				sb.append(")]</p>");
//				
//				Main.game.appendToTextEndStringBuilder(sb.toString());
//			}
//		}
//
//		// Doll:
//		List<GameCharacter> slaveSubs = new ArrayList<>(submissiveParticipants);
//		slaveSubs.removeIf(s->!s.isDollStatue());
//		if(!slaveSubs.isEmpty()) {
//			List<String> names = new ArrayList<>();
//			for(GameCharacter slave : slaveSubs) {
//				names.add(UtilText.parse(slave, "[npc.name]"));
//			}
//			StringBuilder sb = new StringBuilder();
//			sb.append("<p style='text-align:center;'>[style.italicsTerrible(");
//				sb.append("Having been ordered to do so, ");
//				sb.append(Util.stringsToStringList(names, false));
//				if(names.size()>1) {
//					sb.append(" are");
//				} else {
//					sb.append(" is");
//				}
//				sb.append(" remaining frozen in place!");
//			sb.append(")]</p>");
//			
//			Main.game.appendToTextEndStringBuilder(sb.toString());
//		}
//		
//		// Sleeping:
//		slaveSubs = new ArrayList<>(submissiveParticipants);
//		slaveSubs.removeIf(s->!s.isAsleep());
//		if(!slaveSubs.isEmpty()) {
//			List<String> names = new ArrayList<>();
//			for(GameCharacter slave : slaveSubs) {
//				names.add(UtilText.parse(slave, "[npc.name]"));
//			}
//			StringBuilder sb = new StringBuilder();
//			sb.append("<p style='text-align:center;'>[style.italicsTerrible(");
//				sb.append("As ");
//				if(names.size()>1) {
//					sb.append(" they are deep sleepers, ");
//				} else {
//					sb.append(UtilText.parse(slaveSubs.get(0), "[npc.sheIs] a deep sleeper, "));
//				}
//				sb.append(Util.stringsToStringList(names, false));
//				if(names.size()>1) {
//					sb.append(" remain");
//				} else {
//					sb.append(" remains");
//				}
//				sb.append(" asleep!");
//			sb.append(")]</p>");
//			
//			Main.game.appendToTextEndStringBuilder(sb.toString());
//		}
//	}
	
	public static List<GameCharacter> getDominantSpectators() {
		return Main.game.getPlayer().getCompanions();
	}

	public static List<GameCharacter> getSubmissiveSpectators() {
		// Removed in 0.4.1.5, as this was allowing sex with random characters who were on the tile that shouldn't have been involved in sex
//		List<NPC> characters = Main.game.getCharactersPresent();
//		characters.removeAll(Main.game.getPlayer().getCompanions());
//		return new ArrayList<>(characters);
		
//		List<NPC> characters = Main.game.getCharactersPresent();
//		characters.removeAll(Main.game.getPlayer().getCompanions());
//		characters.removeIf(c ->
//			(!c.isSlave() || !c.getOwner().isPlayer()) // Character is not a slave or is a slave that doesn't belong to the player
//			&& (!Main.game.getPlayer().getFriendlyOccupants().contains(c.getId()) || !c.isAttractedTo(Main.game.getPlayer()))); // AND is not a friend or is not attracted to the player
		
		List<NPC> characters = new ArrayList<>();
		for(NPC character : Main.game.getCharactersPresent()) {
			if(((character.isSlave() && character.getOwner().isPlayer()) // Add if character is player's slave
					|| (Main.game.getPlayer().getFriendlyOccupants().contains(character.getId()) && character.isAttractedTo(Main.game.getPlayer()))) // OR character is friend who is attracted to player
				&& !character.isAsleep()) { // AND not asleep
				characters.add(character);
			}
		}
		return new ArrayList<>(characters);
	}
	
	private static boolean enslavementWorked = false;
	public static final DialogueNode DEFAULT_ENSLAVEMENT_DIALOGUE = new DialogueNode("New Slave", "", true) {
		@Override
		public void applyPreParsingEffects() {
			if(!SlaveDialogue.getEnslavementTarget().isSlave() && SlaveDialogue.getEnslavementTarget().isAbleToBeEnslaved() && Main.game.getPlayer().isHasSlaverLicense()) {
				Main.game.getTextEndStringBuilder().append(enslavementTarget.applyEnslavementEffects(Main.game.getPlayer()));
				
			} else {
				Main.game.getTextEndStringBuilder().append(enslavementTarget.incrementAffection(Main.game.getPlayer(), -25));
			}
			
			// Generate content:
			GameCharacter target = enslavementTarget;
			AbstractClothing enslavementClothing = target.getEnslavementClothing();
			UtilText.addSpecialParsingString(enslavementClothing.getName(), true);
			UtilText.addSpecialParsingString(enslavementClothing.getClothingType().isPlural()?"them":"it", false);
			String path = "characters/enslavement";
			if(target instanceof NPCOffspring) {
				path = "characters/offspring/enslavement";
			}
			
			if(!target.isSlave() && target.isAbleToBeEnslaved() && Main.game.getPlayer().isHasSlaverLicense()) {
				if(enslavementClothing.getClothingType().equals(ClothingType.getClothingTypeFromId("innoxia_bdsm_metal_collar"))) {
					Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_SUCCESS_COLLAR", target));
				} else {
					Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_SUCCESS", target));
				}
				enslavementWorked = true;
				
			} else {
				if(target.isSlave()) {
					Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_FAIL_ALREADY_SLAVE", target));
					
				} else if(!target.isAbleToBeEnslaved()) {
					if(target.getSubspecies()==Subspecies.DEMON) {
						Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_FAIL_NOT_WANTED_DEMON", target));
						
					} else {
						Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_FAIL_NOT_WANTED", target));
					}
					
				} else {
					Main.game.appendToTextStartStringBuilder(UtilText.parseFromXMLFile(path, "ENSLAVEMENT_FAIL_NO_LICENSE", target));
				}
				enslavementWorked = false;
			}
			
			// Apply effects after content has been generated due to conditional checks:
			Main.game.getPlayer().addSlave((NPC) enslavementTarget);
			enslavementTarget.setLocation(WorldType.SLAVER_ALLEY, PlaceType.SLAVER_ALLEY_SLAVERY_ADMINISTRATION, true);
		}
		@Override
		public String getContent() {
			return "";
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if(index == 1) {
				if(enslavementWorked) {
					return new Response("Continue",
							"Carry on your way.",
							SlaveDialogue.getFollowupEnslavementDialogue()){
						@Override
						public void effects() {
//							Main.game.getPlayer().addSlave((NPC) enslavementTarget);
//							enslavementTarget.setLocation(WorldType.SLAVER_ALLEY, PlaceType.SLAVER_ALLEY_SLAVERY_ADMINISTRATION, true);
						}
						@Override
						public DialogueNode getNextDialogue(){
							return SlaveDialogue.getFollowupEnslavementDialogue();
						}
					};
					
				} else {
					return new Response("Continue",
							UtilText.parse(SlaveDialogue.getEnslavementTarget(), "That didn't work, but it doesn't mean you're finished with [npc.name] yet!"),
							SlaveDialogue.getFollowupEnslavementDialogue());
				}
				
			} else {
				return null;
			}
		}
	};
	
	public static final DialogueNode FREEDOM_DIALOG = new DialogueNode("Freed Slave", "", true) {
		@Override
		public void applyPreParsingEffects() {
			Main.game.getTextEndStringBuilder().append(enslavementTarget.incrementAffection(Main.game.getPlayer(), -25));
		}
		@Override
		public String getContent() {
			GameCharacter target = enslavementTarget;
			AbstractClothing enslavementClothing = target.getEnslavementClothing();
			UtilText.addSpecialParsingString(enslavementClothing.getName(), true);
			UtilText.addSpecialParsingString(enslavementClothing.getClothingType().isPlural()?"them":"it", false);
			return UtilText.parseFromXMLFile("characters/enslavement", "ENSLAVEMENT_FAIL_FREEDOM_CERTIFICATION", target);
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if (index == 1) {
				return new Response("Continue",
						UtilText.parse(SlaveDialogue.getEnslavementTarget(), "That didn't work, but it doesn't mean you're finished with [npc.name] yet!"),
						SlaveDialogue.getFollowupEnslavementDialogue());
			}
			return null;
		}
	};
	
	private static String getSlaveStartCoreContent() {
		StringBuilder sb = new StringBuilder();
		
		if(!isDollStatue()) {
			if(getSlave().isVisiblyPregnant()) {
				// Pregnant encounters:
				if(!getSlave().isCharacterReactedToPregnancy(Main.game.getPlayer())) {
					sb.append("<p>");
						sb.append("As you look at the [npc.race], it's impossible not to notice the fact that [npc.sheIs] sporting a round belly.");
						sb.append(" [npc.She] absent-mindedly strokes [npc.her] swollen bump as [npc.she] returns your gaze,");
					
					GameCharacter father = getSlave().getPregnantLitter().getFather();
					
					if(father!=null && father.isPlayer()) {
						switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
							case DISLIKE:
								switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
									case DISOBEDIENT:
										sb.append(" not bothering to even to try and conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] spits,"
												+ " [npc.speech(Eugh, it's <i>you</i>. You went and got me pregnant, so I expect some time off work. Fucking asshole...)]");
										break;
									case NEUTRAL:
										sb.append(" trying to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] remarks,"
												+ " [npc.speech(Oh, hello, [npc.pcName]. You got me pregnant, so I'll need some time off work.)]");
										break;
									case OBEDIENT:
										sb.append(" obediently doing [npc.her] very best to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] calls out,"
												+ " [npc.speech(Hello, [npc.pcName]. As I'm sure you can see, you've got me pregnant...)]");
										break;
								}
								break;
							case NEUTRAL:
								switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
									case DISOBEDIENT:
										sb.append(" sighing,"
												+ " [npc.speech(Hi, [npc.pcName]. You got me pregnant, so I'm going to need to take it easy for a while, ok?)]");
										break;
									case NEUTRAL:
										sb.append(" sighing,"
												+ " [npc.speech(Hello, [npc.pcName]. You got me pregnant...)]");
										break;
									case OBEDIENT:
										sb.append(" sighing,"
												+ " [npc.speech(Hello, [npc.pcName]. You got me pregnant... I'll make sure to take good care of our children!)]");
										break;
								}
								break;
							case LIKE:
								switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
									case DISOBEDIENT:
										sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
												+ " [npc.speech([npc.PcName]! Look! You got me pregnant, isn't it wonderful?! I'm going to need to take it easy for a while, so that I can take good care of myself, ok?)]");
										break;
									case NEUTRAL:
										sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
												+ " [npc.speech([npc.PcName]! You got me pregnant, isn't it wonderful?! I'll make sure to take good care of our children!)]");
										break;
									case OBEDIENT:
										sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
												+ " [npc.speech(Hello, [npc.pcName]! You got me pregnant! I'll make sure to take good care of our children!)]");
										break;
								}
								break;
						}
						sb.append("</p>");
						sb.append(
								"<p>"
									+ "You [pc.step] up to your slave and run your [pc.hands] over [npc.her] pregnant belly."
									+ " Smiling reassuringly at the mother of your children, you tell [npc.herHim] that Lilaya will be able to help [npc.herHim] give birth once the time is right."
								+ "</p>"
								+ "<p>"
									+ "[npc.Name] acknowledges this with a nod, and so after you've stroked and caressed [npc.her] belly for a little while longer, you [pc.step] back and wonder what to do next..."
								+ "</p>");
						
					} else {
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								sb.append(" sighing,"
										+ " [npc.speech(Hi, [npc.pcName]. "+(father==null?"I ended up getting pregnant":father.getName("A")+" got me pregnant")+", so I'm going to take it easy for a while. Get one of the other slaves to cover for me, ok?)]");
								break;
							case NEUTRAL:
								sb.append(" sighing,"
										+ " [npc.speech(Hi, [npc.pcName]. "+(father==null?"I ended up getting pregnant":father.getName("A")+" got me pregnant")+", so I'm going to need to take it easy for a while, ok?)]");
								break;
							case OBEDIENT:
								sb.append(" obediently informing you of what happened,"
										+ " [npc.speech(Hello, [npc.pcName]. "+(father==null?"I ended up getting pregnant":father.getName("A")+" got me pregnant")+", but I won't let it get in the way of my duties!)]");
								break;
						}
						sb.append("</p>");
						sb.append(
								"<p>"
									+ "You [pc.step] up to your slave and run your [pc.hands] over [npc.her] pregnant belly."
									+ " Smiling reassuringly at the [npc.race], you tell [npc.herHim] that Lilaya will be able to help [npc.herHim] give birth once the time is right."
								+ "</p>"
								+ "<p>"
									+ "[npc.Name] acknowledges this with a nod, and so after you've stroked and caressed [npc.her] belly for a little while longer, you [pc.step] back and wonder what to do next..."
								+ "</p>");
					}
				
				} else {
					sb.append(
							"<p>"
								+ "As you look at the [npc.race], you see that [npc.sheIs] still sporting a round belly, and [npc.she] absent-mindedly strokes [npc.her] pregnant bump as [npc.she] returns your gaze,");
					switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
						case DISLIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" not bothering to even to try and conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] spits,"
											+ "[npc.speech(Eugh, it's <i>you</i>. What the hell do you want now?!)]");
									break;
								case NEUTRAL:
									sb.append(" trying to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] remarks,"
											+ "[npc.speech(Oh, hello, [npc.pcName]. What is it that you want?)]");
									break;
								case OBEDIENT:
									sb.append(" obediently doing [npc.her] very best to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] calls out,"
											+ " [npc.speech(Hello, [npc.pcName]. What can I do for you?)]");
									break;
							}
							break;
						case NEUTRAL:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" sighing,"
											+ " [npc.speech(Hi, [npc.pcName]. I'm taking it easy, what with the pregnancy and all, ok?)]");
									break;
								case NEUTRAL:
									sb.append(" sighing,"
											+ " [npc.speech(Hello, [npc.pcName]. What can I do for you?)]");
									break;
								case OBEDIENT:
									sb.append(" sighing,"
											+ " [npc.speech(Hello, [npc.pcName]. Is there anything that I can do for you?)]");
									break;
							}
							break;
						case LIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hi, [npc.pcName]! How are you doing?! I'm taking it easy at the moment, so that I can take good care of myself, ok?)]");
									break;
								case NEUTRAL:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hello, [npc.pcName]! I'm taking good care of myself! How are you?)]");
									break;
								case OBEDIENT:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hello, [npc.pcName]! Is there anything I can do for you?)]");
									break;
							}
							break;
					}
					sb.append("</p>");
					sb.append(
							"<p>"
								+ "You [pc.step] up to your slave and run your [pc.hands] over [npc.her] pregnant belly."
								+ " Smiling reassuringly at the [npc.race], you tell [npc.herHim] that Lilaya will be able to help [npc.herHim] give birth once the time is right."
							+ "</p>"
							+ "<p>"
								+ "[npc.Name] acknowledges this with a nod, and so after you've stroked and caressed [npc.her] belly for a little while longer, you [pc.step] back and wonder what to do next..."
							+ "</p>");
				}
				
			} else { // Standard repeat encounter:

				sb.append("<p>");
				
				if(isDoll()) {
					sb.append("[npc.speech(Good [style.morning], [pc.name],)] [npc.she] says in [npc.her] slightly robotic voice, [npc.speech(How may I serve you?)]");
					
				} else {
					sb.append("As you look at the [npc.race], [npc.she] returns your gaze,");
					
					switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
						case DISLIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" not bothering to even to try to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] spits,"
											+ "[npc.speech(Eugh, it's <i>you</i>. What the hell do you want now?!)]");
									break;
								case NEUTRAL:
									sb.append(" trying to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] remarks,"
											+ "[npc.speech(Oh, hello, [npc.pcName]. What is it that you want?)]");
									break;
								case OBEDIENT:
									sb.append(" obediently doing [npc.her] very best to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] calls out,"
											+ " [npc.speech(Hello, [npc.pcName]. What can I do for you?)]");
									break;
							}
							break;
						case NEUTRAL:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" sighing,"
											+ " [npc.speech(Hi, [npc.pcName]. What do you want?)]");
									break;
								case NEUTRAL:
									sb.append(" sighing,"
											+ " [npc.speech(Hello, [npc.pcName]. What can I do for you?)]");
									break;
								case OBEDIENT:
									sb.append(" sighing,"
											+ " [npc.speech(Hello, [npc.pcName]. Is there anything that I can do for you?)]");
									break;
							}
							break;
						case LIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hi, [npc.pcName]! Oh, I'm so happy to see you again! I've been on my best behaviour!)]");
									break;
								case NEUTRAL:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hello, [npc.pcName]! How are you? Is there anything I can do for you?)]");
									break;
								case OBEDIENT:
									sb.append(" a huge smile breaking out across [npc.her] face as [npc.she] joyously calls out,"
											+ " [npc.speech(Hello, [npc.pcName]! Is there anything I can do for you?)]");
									break;
							}
							break;
					}
				}
				
				sb.append("</p>");
				sb.append(
						"<p>"
							+ "With your slave waiting to hear what your command will be, you wonder what to do with [npc.herHim] now..."
						+ "</p>");
			}
		}
		
		sb.append(getFooterText());
		
		return UtilText.parse(getSlave(), sb.toString());
	}
	
	public static final DialogueNode SLAVE_START_NO_CONTENT = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		@Override
		public String getLabel() {
			return Main.game.getPlayer().getLocationPlace().getName();
		}
		@Override
		public String getContent() {
			return "";
		}
		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	private static Response getWakeUpResponse() {
		return new Response(UtilText.parse(getSlave(), "Wake [npc.herHim]"),
				UtilText.parse(getSlave(),
						"Wake [npc.name] up so that you can interact with [npc.herHim]."
						+ "<br/>[style.italicsMinorBad([npc.She] will be a little annoyed with you for having woken [npc.herHim] up...)]"),
				SLAVE_START_NO_CONTENT) {
			@Override
			public void effects() {
				getSlave().wakeUp();
				
				Main.game.appendToTextEndStringBuilder("<p>");
					Main.game.appendToTextEndStringBuilder("Wanting to interact with [npc.name], you [pc.step] over to where [npc.sheIs] sleeping and ");
					if(Main.game.getPlayer().isMute()) {
						Main.game.appendToTextEndStringBuilder(" give [npc.herHim] a shake.");
					} else {
						Main.game.appendToTextEndStringBuilder(" call out [npc.her] name.");
					}
					if(getSlave().hasTrait(Perk.HEAVY_SLEEPER, true)) {
						Main.game.appendToTextEndStringBuilder(" Being such a heavy sleeper, this isn't enough to wake [npc.herHim],"
								+ " and you have to give [npc.herHim] several forceful shoves before [npc.she] finally begins to sleepily open [npc.her] [npc.eyes].");
					}
					Main.game.appendToTextEndStringBuilder(" Waking to see you standing over [npc.herHim], [npc.she] quickly stands up and turns to face you.");
				Main.game.appendToTextEndStringBuilder("</p>");
				Main.game.appendToTextEndStringBuilder(UtilText.parse(getSlave(), "<p style='text-align:center;'>[style.italicsMinorBad([npc.Name] doesn't appreciate being woken up...)]</p>"));
				Main.game.appendToTextEndStringBuilder(getSlave().incrementAffection(Main.game.getPlayer(), -1));
				Main.game.appendToTextEndStringBuilder(getSlaveStartCoreContent());
			}
		};
	}
	
	public static final DialogueNode SLAVE_START = new DialogueNode("", ".", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		@Override
		public String getLabel() {
			return Main.game.getPlayer().getLocationPlace().getName();
		}
		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append("<p>");
				if(getSlave().isAsleep()) {
					UtilText.nodeContentSB.append("As [npc.name] is sleeping at this time, you'll need to wake [npc.herHim] up before interacting with [npc.herHim].");
					UtilText.nodeContentSB.append(" Doing so is sure to annoy [npc.herHim] at least a little...");
				} else {
					if(isDoll()) {
						UtilText.nodeContentSB.append("Deciding that you'd like to interact with [npc.name], you [pc.walk] up to your doll and get [npc.her] attention.");
						if(isDollStatue()) {
							UtilText.nodeContentSB.append(" As you've ordered [npc.herHim] to act as a statue, [npc.she] dutifully ignores your approach and remains frozen in place.");
						}
					} else {
						UtilText.nodeContentSB.append("Deciding that you'd like to interact with [npc.name], you [pc.walk] over to [npc.herHim] and get [npc.her] attention.");
					}
				}
			UtilText.nodeContentSB.append("</p>");

			if(!getSlave().isAsleep() && !isDollStatue()) {
				UtilText.nodeContentSB.append(getSlaveStartCoreContent());
			}
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			if(index == 0) {
				return "Interaction";
			} else if(index == 1) {
				return UtilText.parse("[style.colourSex(Sex)]");
			} else if(index == 2) {
				return UtilText.parse("[style.colourCompanion(Manage)]");
			}
			return null;
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			if(responseTab == 0) {
				if(index == 0) {
					return new Response("Leave",
							UtilText.parse(getSlave(),
									isDollStatue()
										?"Leave [npc.name] frozen in place and continue on your way."
										:(getSlave().isAsleep()
											?"Leave [npc.name] to sleep..."
											:"Tell [npc.name] that you'll catch up with [npc.herHim] some other time.")),
							Main.game.getDefaultDialogue(false)) {
						@Override
						public DialogueNode getNextDialogue() {
							return Main.game.getDefaultDialogue(false);
						}
						@Override
						public void effects() {
							Main.game.setResponseTab(0);
							applyReactionReset();
							Main.game.getDialogueFlags().setManagementCompanion(null);
						}
					};
				}
				
				if(isDollStatue()) {
					if(index == 1) {
						return new Response("Stop statue",
								UtilText.parse(getSlave(), "Tell [npc.name] to temporarily stop acting like a statue so that you can interact with [npc.herHim]."),
								SLAVE_START_NO_CONTENT) {
							@Override
							public void effects() {
								dollStatueInterrupted = true;
								Main.game.appendToTextEndStringBuilder("<p>");
									Main.game.appendToTextEndStringBuilder("Wanting to interact with [npc.name] without [npc.herHim] continuing to act like an unresponsive statue, you give [npc.herHim] permission to temporarily move freely once again.");
								Main.game.appendToTextEndStringBuilder("</p>");
								Main.game.appendToTextEndStringBuilder(getSlaveStartCoreContent());
							}
						};
					}
					
				} else if(getSlave().isAsleep()) {
					if(index == 1) {
						return getWakeUpResponse();
					}

				} else {
					if(index == 1) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveBackground)) {
							return new Response("Background",
									UtilText.parse(getSlave(),
											isDoll()
												?"Ask [npc.name] what [npc.she] can remember of being created."
												:"Ask [npc.name] about [npc.her] past life."),
									SLAVE_PROGRESSION) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveBackground);
									if(!isDoll()) {
										Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 3));
									}
								}
							};
						} else {
							return new Response("Background", UtilText.parse(getSlave(), "You've already talked with [npc.name] about this today..."), null);
						}
						
					} else if(index == 2) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveSmallTalk)) {
							return new Response("Small talk", UtilText.parse(getSlave(), "Chat about this and that with [npc.name]."), SLAVE_MINOR) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveSmallTalk);
									if(!isDoll()) {
										switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
											case DISLIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -1f));
												break;
											case NEUTRAL:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 2f));
												break;
											case LIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 4f));
												break;
										}
									}
								}
							};
						} else {
							return new Response("Small talk", UtilText.parse(getSlave(), "You've already spent time talking with [npc.name] today."), null);
						}
						
					} else if(index == 3
							&& getSlave().equals(Main.game.getNpc(Scarlett.class))
							&& !Main.getProperties().hasValue(PropertyValue.companionContent)
							&& Main.game.getPlayer().getQuest(QuestLine.ROMANCE_HELENA)==Quest.ROMANCE_HELENA_4_SCARLETTS_RETURN) {
						if(Main.game.getNpc(Helena.class).getLocationPlace().getPlaceType()!=PlaceType.SLAVER_ALLEY_SCARLETTS_SHOP) {
							return new Response("Helena",
									UtilText.parse(getSlave(), "Helena's shop is currently closed, so you're unable to take [npc.name] to her at the moment..."),
									null);
						}
						return new Response("Helena",
								UtilText.parse(getSlave(), "Accompany [npc.name] to Helena's store in Slaver Alley."),
								ScarlettsShop.ROMANCE_SHOP_CORE) {
							@Override
							public Colour getHighlightColour() {
								return PresetColour.QUEST_RELATIONSHIP;
							}
							@Override
							public void effects() {
	//							Main.game.getPlayer().addCompanion(getSlave());
								applyReactionReset();
								Main.game.getDialogueFlags().setManagementCompanion(null);
								// Move them both here to make sure they haven't gone due to time ticking over into night time when player arrives:
								Main.game.getPlayer().setLocation(WorldType.SLAVER_ALLEY, PlaceType.SLAVER_ALLEY_SCARLETTS_SHOP);
								Main.game.getNpc(Scarlett.class).setLocation(WorldType.SLAVER_ALLEY, PlaceType.SLAVER_ALLEY_SCARLETTS_SHOP);
								Main.game.getNpc(Helena.class).setLocation(WorldType.SLAVER_ALLEY, PlaceType.SLAVER_ALLEY_SCARLETTS_SHOP);
							}
						};
						
					} else if(index == 5 && Main.getProperties().hasValue(PropertyValue.companionContent)) {
						if(!Main.game.getPlayer().hasCompanion(getSlave())) {
							if(!getSlave().isCompanionAvailable(Main.game.getPlayer())) {
								return new Response("Add to party",
										UtilText.parse(getSlave(), "[npc.Name] cannot be added to your party!"),
										null);
									
							} else if(Main.game.getPlayer().canHaveMoreCompanions()) {
								return new Response("Add to party",
										UtilText.parse(getSlave(), "Command [npc.name] to start following you around."),
										SLAVE_START){
									@Override
									public void effects() {
										applyReactionReset();
										Main.game.getPlayer().addCompanion(getSlave());
									}
								};
							} else {
								return new Response("Add to party",
										"You are already at your party limit!",
										null);
							}
						} else {
							return new Response("Remove from party",
									UtilText.parse(getSlave(), "Command [npc.name] to leave your party."),
									SLAVE_START){
								@Override
								public void effects() {
									applyReactionReset();
									Main.game.getPlayer().removeCompanion(getSlave());
								}
							};
						}
						
					} else if(index == 6) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveEncourage)) {
							if(getCurrentJob()==SlaveJob.IDLE) {
								return new Response("Work",
										UtilText.parse(getSlave(),
												"[npc.Name] isn't currently doing any work...<br/>[style.italicsMinorBad(You can't ask [npc.name] about [npc.her] work while [npc.sheIs] idle!)]"),
										null);
								
							} else {
								return new Response("Work", UtilText.parse(getSlave(), "Ask [npc.name] about how [npc.her] work is going."), SLAVE_ENCOURAGE) {
									@Override
									public void effects() {
										applyReactionReset();
										getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveEncourage);
										if(!isDoll()) {
											switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
												case DISLIKE:
													Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 0.5f));
													Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(0.1f));
													break;
												case NEUTRAL:
													Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 2f));
													Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(1f));
													break;
												case LIKE:
													Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 4f));
													Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(2f));
													break;
											}
										}
									}
								};
							}
							
						} else {
							return new Response("Work", UtilText.parse(getSlave(), "You've talked to [npc.name] about [npc.her] work today."), null);
						}
						
					} else if(index == 7) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveHug)) {
							return new Response("Hug", UtilText.parse(getSlave(), "Hug [npc.name]."), SLAVE_HUG) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveHug);
	
									if(!isDoll()) {
										switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
											case DISLIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -2));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-1f));
												break;
											case NEUTRAL:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 2));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-1));
												break;
											case LIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 5));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-2));
												break;
										}
									}
								}
							};
						} else {
							return new Response("Hug", UtilText.parse(getSlave(), "You've already spent time hugging [npc.name] today."), null);
						}
						
					} else if(index == 8) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlavePettings)) {
							return new Response("Pettings", UtilText.parse(getSlave(), "Give [npc.name] some loving pettings."), SLAVE_PETTINGS) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlavePettings);
									
									if(!isDoll()) {
										switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
											case DISLIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -2));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-1f));
												break;
											case NEUTRAL:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 2));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-1));
												break;
											case LIKE:
												Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 5));
												Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-2));
												break;
										}
									}
								}
							};
						} else {
							return new Response("Pettings", UtilText.parse(getSlave(), "You've already spent time petting [npc.name] today."), null);
						}
						
					} else if(index == 9) {
						if(Main.game.getPlayer().hasItemType(ItemType.PRESENT)) {
							if(isDoll()) {
								return new Response("Give Present", UtilText.parse(getSlave(), "As a doll, [npc.name] has no need or appreciation for presents..."), null);
							}
							return new Response("Give Present", UtilText.parse(getSlave(), "Give [npc.name] the present that you're carrying."), SLAVE_PRESENT) {
								@Override
								public void effects() {
									applyReactionReset();
									Main.game.getPlayer().removeItem(Main.game.getItemGen().generateItem(ItemType.PRESENT));
	
									if(!isDoll()) {
										Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 10));
										Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(-2));
									}
								}
							};
							
						} else {
							return null;
						}
						
					} else if(index == 11) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveInspect)) {
							return new Response("Inspect", UtilText.parse(getSlave(), "Make [npc.name] strip and parade around [npc.her] room for your inspection."), SLAVE_INSPECT) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveInspect);
		
									getSlave().setAreaKnownByCharacter(CoverableArea.ANUS, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.ASS, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.BREASTS, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.MOUND, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.MOUTH, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.NIPPLES, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.PENIS, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.TESTICLES, Main.game.getPlayer(), true);
									getSlave().setAreaKnownByCharacter(CoverableArea.VAGINA, Main.game.getPlayer(), true);
	
									if(!isDoll()) {
										if(getSlave().getFetishDesire(Fetish.FETISH_MASOCHIST).isPositive() || getSlave().getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()) {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 10));
										} else {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -5));
										}
										Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(5));
									}
								}
							};
						} else {
							return new Response("Inspect", UtilText.parse(getSlave(), "You've already inspected [npc.name] today."), null);
						}
						
					} else if(index == 12) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveSpanking)) {
							return new Response("Spanking", UtilText.parse(getSlave(), "Bend [npc.name] over your knee and give [npc.herHim] a rough spanking."), SLAVE_SPANKING) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveSpanking);
									if(!isDoll()) {
										if(getSlave().getFetishDesire(Fetish.FETISH_MASOCHIST).isPositive()) {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 10));
										} else {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -5));
										}
										Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(10));
									}
								}
							};
						} else {
							return new Response("Spanking", UtilText.parse(getSlave(), "You've already spanked [npc.name] today."), null);
						}
						
					} else if(index == 13) {
						if(!getSlave().NPCFlagValues.contains(NPCFlagValue.flagSlaveMolest)) {
							return new Response("Molest", UtilText.parse(getSlave(), "Make [npc.name] sit still as you grope and molest [npc.her] body."), SLAVE_MOLEST) {
								@Override
								public void effects() {
									applyReactionReset();
									getSlave().NPCFlagValues.add(NPCFlagValue.flagSlaveMolest);
	
									if(!isDoll()) {
										if(getSlave().isAttractedTo(Main.game.getPlayer())
												&& (getSlave().getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive() || getSlave().getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive())) {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), 10));
											
										} else if(!getSlave().isAttractedTo(Main.game.getPlayer()) && !getSlave().hasFetish(Fetish.FETISH_SUBMISSIVE) && !getSlave().hasFetish(Fetish.FETISH_NON_CON_SUB)) {
											Main.game.getTextEndStringBuilder().append(getSlave().incrementAffection(Main.game.getPlayer(), -10));
										}
										Main.game.getTextEndStringBuilder().append(getSlave().incrementObedience(10));
									}
								}
							};
						} else {
							return new Response("Molest", UtilText.parse(getSlave(), "You've already molested [npc.name] today."), null);
						}
						
					}
				}
				
				return null;
			
			} else if(responseTab == 1) {
				if(index == 0) {
					return new Response("Leave", 
							UtilText.parse(getSlave(),
									isDollStatue()
										?"Leave [npc.name] frozen in place and continue on your way."
										:(getSlave().isAsleep()
											?"Leave [npc.name] to sleep..."
											:"Tell [npc.name] that you'll catch up with [npc.herHim] some other time."))
							, Main.game.getDefaultDialogue(false)) {
						@Override
						public DialogueNode getNextDialogue() {
							return Main.game.getDefaultDialogue(false);
						}
						@Override
						public void effects() {
							Main.game.setResponseTab(0);
							applyReactionReset();
							Main.game.getDialogueFlags().setManagementCompanion(null);
						}
					};
				}
				
				if(Main.game.getPlayer().getLocationPlace().getPlaceUpgrades().contains(PlaceUpgrade.LILAYA_MILKING_ROOM)) {
					if(index == 1) {
						if((!characterForSex.isSlave() || !characterForSex.getOwner().isPlayer())
								&& !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Sex", UtilText.parse(characterForSex, "[npc.Name] is not attracted to you, and you cannot force [npc.herHim] to have sex with you..."), null);
							
						} else if(Main.game.isNonConEnabled() && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new ResponseSex("Rape", UtilText.parse(characterForSex, "[npc.Name] is definitely not interested in having sex with you, but it's not like [npc.sheHasFull] a choice in the matter..."), 
									false, false,
									new SMMilkingStall(
											Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotMilkingStall.BEHIND_MILKING_STALL)),
											Util.newHashMapOfValues(new Value<>(characterForSex, SexSlotMilkingStall.LOCKED_IN_MILKING_STALL))),
									getDominantSpectators(),
									getSubmissiveSpectators(),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "RAPE_START_MILKING_ROOM", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									if(!characterForSex.isDoll()) {
										if(characterForSex.getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive()) {
											Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), 5));
										} else {
											Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), -25));
										}
									}
								}
							};
							
						} else {
							return new ResponseSex("Sex", UtilText.parse(characterForSex, "Have sex with [npc.name]."), 
									true, false,
									new SMMilkingStall(
											Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexSlotMilkingStall.BEHIND_MILKING_STALL)),
											Util.newHashMapOfValues(new Value<>(characterForSex, SexSlotMilkingStall.LOCKED_IN_MILKING_STALL))),
									getDominantSpectators(),
									getSubmissiveSpectators(),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "SEX_START_MILKING_ROOM", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									if(!characterForSex.isDoll()) {
										Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), 5));
									}
								}
							};
						}
					}
					
				} else if(getSlave().isAsleep()) {
					if(index == 1) {
						if((!characterForSex.isSlave() || !characterForSex.getOwner().isPlayer())
								&& !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Sleep sex", UtilText.parse(characterForSex, "[npc.Name] is not attracted to you, and you cannot force [npc.herHim] to have sex with you..."), null);
							
						} else if(Main.game.isNonConEnabled() && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							if(!getSlave().hasTrait(Perk.HEAVY_SLEEPER, true)) {
								return new Response("Sleep rape", UtilText.parse(characterForSex, "[npc.Name] is not a heavy sleeper, and so you can't have sleep sex with [npc.herHim]..."), null);
							}
							return new ResponseSex("Sleep rape",
									UtilText.parse(characterForSex,
											"[npc.Name] is definitely not interested in having sex with you, but it's not like [npc.sheHasFull] a choice in the matter."
											+ " As [npc.sheIs] a heavy sleeper, you can gently fuck [npc.herHim] without waking [npc.herHim] up..."), 
									false, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex),
											new ArrayList<>(),
											new ArrayList<>(),
											Util.newArrayListOfValues(ResponseTag.PREFER_MISSIONARY),
											false,
											Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexPace.DOM_GENTLE))),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "RAPE_START", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
								}
							};
						
						} else {
							if(!getSlave().hasTrait(Perk.HEAVY_SLEEPER, true)) {
								return new Response("Sleep sex", UtilText.parse(characterForSex, "[npc.Name] is not a heavy sleeper, and so you can't have sleep sex with [npc.herHim]..."), null);
							}
							return new ResponseSex("Sleep sex",
									UtilText.parse(characterForSex, "As [npc.sheIs] a heavy sleeper, you can gently fuck [npc.name] without waking [npc.herHim] up..."), 
									true, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex),
											new ArrayList<>(),
											new ArrayList<>(),
											Util.newArrayListOfValues(ResponseTag.PREFER_MISSIONARY),
											false,
											Util.newHashMapOfValues(new Value<>(Main.game.getPlayer(), SexPace.DOM_GENTLE))),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "SEX_START", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
								}
							};
						}
						
					}
					
				} else {
					if(index == 1) {
						if((!characterForSex.isSlave() || !characterForSex.getOwner().isPlayer())
								&& !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Sex", UtilText.parse(characterForSex, "[npc.Name] is not attracted to you, and you cannot force [npc.herHim] to have sex with you..."), null);
							
						} else if(Main.game.isNonConEnabled() && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new ResponseSex("Rape", UtilText.parse(characterForSex, "[npc.Name] is definitely not interested in having sex with you, but it's not like [npc.sheHasFull] a choice in the matter..."), 
									false, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex),
											(characterForSex.hasSlavePermissionSetting(SlavePermissionSetting.GENERAL_CRAWLING)
												?Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY)
												:new ArrayList<>()), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "RAPE_START", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									if(!characterForSex.isDoll()) {
										if(characterForSex.getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive()) {
											Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), 5));
										} else {
											Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), -25));
										}
									}
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
								}
							};
						
						} else {
							return new ResponseSex("Sex", UtilText.parse(characterForSex, "Have sex with [npc.name]."), 
									true, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex),
											isDollStatue()
												?Util.newArrayListOfValues(getDollSexTag())
												:(characterForSex.hasSlavePermissionSetting(SlavePermissionSetting.GENERAL_CRAWLING)
													?Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY)
													:new ArrayList<>()),
											false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "SEX_START", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									if(!characterForSex.isDoll()) {
										Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), 5));
									}
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
								}
							};
						}
						
					} else if(index == 2) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Spitroast (front)", "You'd need a third person to be present in order to get a spitroast going...", null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Spitroast (front)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSexSecondary.isDollStatue()) {
							return new Response("Spitroast (front)",
									UtilText.parse(characterForSexSecondary,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Spitroast (front)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if(!characterForSexSecondary.isAttractedTo(characterForSex)) {
							return new Response("Spitroast (front)",
									UtilText.parse(characterForSexSecondary, characterForSex,
											"[npc.Name] is not attracted to [npc2.name], and so it would not be possible to make [npc.herHim] take a dominant position in order to fuck [npc2.herHim]..."),
									null);
								
						} else if((!Main.game.isNonConEnabled() || !characterForSex.isSlave()) && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Spitroast (front)",
									UtilText.parse(characterForSex,
											"[npc.Name] is not attracted to you, and so would not be willing to be in a threesome position in which [npc.she] interacts with you..."),
									null);
							
						} else if((!Main.game.isNonConEnabled() || !characterForSex.isSlave()) && !characterForSex.isAttractedTo(characterForSexSecondary)) {
							return new Response("Spitroast (front)",
									UtilText.parse(characterForSexSecondary, characterForSex,
											"[npc2.Name] is not attracted to [npc.name], and so would not be willing to be in a threesome position in which [npc2.she] interacts with [npc.herHim]..."),
									null);
							
						} else {
							boolean isRape = !characterForSex.isAttractedTo(Main.game.getPlayer()) && !characterForSex.isAttractedTo(characterForSexSecondary);
							return new ResponseSex(
									isRape
										?"Spitroast rape (front)"
										:"Spitroast (front)",
									UtilText.parse(characterForSex, characterForSexSecondary, "Move around in front of [npc.name] so that you can use [npc.her] mouth while [npc2.name] takes [npc.her] rear."),
									null, null, null, null, null, null,
									!isRape, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(characterForSexSecondary, Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex),
											Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SPITROAST_FRONT_START", characterForSex, characterForSexSecondary)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
								}
							};
						}
						
					
					} else if(index == 3) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Spitroast (behind)", "You'd need a third person to be present in order to get a spitroast going...", null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Spitroast (behind)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSexSecondary.isDollStatue()) {
							return new Response("Spitroast (behind)",
									UtilText.parse(characterForSexSecondary,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Spitroast (behind)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if(!characterForSexSecondary.isAttractedTo(characterForSex)) {
							return new Response("Spitroast (behind)",
									UtilText.parse(characterForSexSecondary, characterForSex,
											"[npc.Name] is not attracted to [npc2.name], and so it would not be possible to make [npc.herHim] take a dominant position in order to fuck [npc2.herHim]..."),
									null);
								
						} else {
							 if((!Main.game.isNonConEnabled() || !characterForSex.isSlave()) && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
								return new Response("Spitroast (behind)",
										UtilText.parse(characterForSex,
												"[npc.Name] is not attracted to you, and so would not be willing to be in a threesome position in which [npc.she] interacts with you..."),
										null);
								
							} else if((!Main.game.isNonConEnabled() || !characterForSex.isSlave()) && !characterForSex.isAttractedTo(characterForSexSecondary)) {
								return new Response("Spitroast (behind)",
										UtilText.parse(characterForSexSecondary, characterForSex,
												"[npc2.Name] is not attracted to [npc.name], and so would not be willing to be in a threesome position in which [npc2.she] interacts with [npc.herHim]..."),
										null);
								
							} else {
								boolean isRape = !characterForSex.isAttractedTo(Main.game.getPlayer()) && !characterForSex.isAttractedTo(characterForSexSecondary);
								return new ResponseSex(
										isRape
											?"Spitroast rape (behind)"
											:"Spitroast (behind)",
										UtilText.parse(characterForSex, characterForSexSecondary, "Move around behind [npc.name] so that you can use [npc.her] rear while [npc2.name] takes [npc.her] mouth."),
										null, null, null, null, null, null,
										!isRape, false,
										getGenericSlaveSexManager(
												Util.newArrayListOfValues(Main.game.getPlayer(), characterForSexSecondary),
												Util.newArrayListOfValues(characterForSex),
												Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
										getAfterSexDialogue(),
										UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SPITROAST_BEHIND_START", characterForSex, characterForSexSecondary)) {
									@Override
									public void effects() {
										applyReactionReset();
										//applyImmobilisationText(Util.newArrayListOfValues(characterForSex));
									}
								};
							}
						}
					
					} else if(index == 4) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Side-by-side (as dom)", "You'd need a third person to be present in order to get a spitroast going...", null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Side-by-side (as dom)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Side-by-side (as dom)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if((!Main.game.isNonConEnabled() || !characterForSexSecondary.isSlave()) && !characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Side-by-side (as dom)",
									UtilText.parse(characterForSexSecondary,
											"[npc.Name] is not attracted to you, and so would not be willing to be in a threesome position in which [npc.she] interacts with you..."),
									null);
								
						} else if((!Main.game.isNonConEnabled() || !characterForSex.isSlave()) && !characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Side-by-side (as dom)",
									UtilText.parse(characterForSex,
											"[npc.Name] is not attracted to you, and so would not be willing to be in a threesome position in which [npc.she] interacts with you..."),
									null);
							
						} else {
							boolean isRape = !characterForSex.isAttractedTo(Main.game.getPlayer()) || !characterForSexSecondary.isAttractedTo(Main.game.getPlayer());
							return new ResponseSex(
									isRape
										?"Side-by-side rape (as dom)"
										:"Side-by-side (as dom)",
									UtilText.parse(characterForSex, characterForSexSecondary, "Push [npc.name] and [npc2.name] down onto all fours, before kneeling behind [npc.name], ready to fuck them both side-by-side."),
									null, null, null, null, null, null,
									!isRape, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(characterForSex, characterForSexSecondary),
											Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SIDE_BY_SIDE_START", characterForSex, characterForSexSecondary)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(characterForSex, characterForSexSecondary));
								}
							};
						}
						
					} else if(index==5) {
						if(charactersPresent.size()<3) {
							return new Response("Gangbang (as dom)",
									"There need to be at least three characters present in order to start a gangbang.",
									null);
							
						} else if (charactersPresent.stream().anyMatch(c->c.isAsleep())) {
							boolean soloSleeper = charactersPresent.stream().filter(c->c.isAsleep()).count()==1;
							String names = Util.charactersToStringListOfNames(charactersPresent.stream().filter(c->c.isAsleep()).collect(Collectors.toList()));
							return new Response("Gangbang (as dom)",
											names+" "+(soloSleeper?"needs":"need")+" to be awake to participate in a gangbang!",
									null);
							
						} else {
							boolean isRape = charactersPresent.stream().anyMatch(c->!c.isAttractedTo(Main.game.getPlayer()));
							// Handle attraction/non-con:
							if(isRape) {
								if(charactersPresent.stream().anyMatch(c->!c.isSlave())) {
									List<GameCharacter> nonSlaves = new ArrayList<>();
									for(GameCharacter character : charactersPresent) {
										if(!character.isSlave() && !character.isAttractedTo(Main.game.getPlayer())) {
											nonSlaves.add(character);
										}
									}
									String isAre = nonSlaves.size()==1?"is":"are";
									String slavePural = nonSlaves.size()==1?"slave":"slaves";
									String them = nonSlaves.size()==1?UtilText.parse(nonSlaves.get(0), "[npc.herHim]"):"them";
									return new Response("Gangbang (as dom)",
											"As "+Util.charactersToStringListOfNames(nonSlaves)+" "+isAre+" not attracted to you, and "+isAre+" not your "+slavePural+", you cannot have a gangbang with "+them+".",
											null);
									
								} else if(!Main.game.isNonConEnabled()) {
									List<GameCharacter> nonAttracted = new ArrayList<>();
									for(GameCharacter character : charactersPresent) {
										if(!character.isAttractedTo(Main.game.getPlayer())) {
											nonAttracted.add(character);
										}
									}
									String isAre = nonAttracted.size()==1?"is":"are";
									String them = nonAttracted.size()==1?UtilText.parse(nonAttracted.get(0), "[npc.herHim]"):"them";
									return new Response("Gangbang (as dom)",
											"As "+Util.charactersToStringListOfNames(nonAttracted)+" "+isAre+" not attracted to you, you cannot have a gangbang with "+them+".",
											null);
								}
							}
							
							List<GameCharacter> nonAttracted = new ArrayList<>();
							for(GameCharacter character : charactersPresent) {
								if(!character.isAttractedTo(Main.game.getPlayer())) {
									nonAttracted.add(character);
								}
							}
							String isAre = nonAttracted.size()==1?"is":"are";
							String them = nonAttracted.size()==1?UtilText.parse(nonAttracted.get(0), "[npc.herHim]"):"them";
							List<GameCharacter> sexParticipants = new ArrayList<GameCharacter>(charactersPresent);
							return new ResponseSex(
									isRape
										?"Gangbang rape (as dom)"
										:"Gangbang (as dom)",
									"Have sex with "+Util.charactersToStringListOfNames(sexParticipants)+"."
										+ (isRape
											?" As "+Util.charactersToStringListOfNames(nonAttracted)+" "+isAre+" not attracted to you, you will have to force "+them+" into it..."
											:""),//TODO sleeping
									null, null, null, null, null, null,
									!isRape, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(Main.game.getPlayer()),
											sexParticipants,
											Util.newArrayListOfValues(), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), sexParticipants.size()==3?"SEX_GANGBANG_3":"SEX_GANGBANG_4", sexParticipants)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(sexParticipants);
								}
							};
						}
						
					} else if(index == 6) {
						if (characterForSex.isDollStatue()) {
							return new Response("Submissive sex",
									UtilText.parse(characterForSex,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if(!characterForSex.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Submissive sex",
									UtilText.parse(characterForSex, 
										"[npc.Name] is not attracted to you,"
										+ (Main.game.isNonConEnabled() && characterForSex.isSlave()
												?" so if you wanted to have sex with [npc.herHim], you'd need to rape [npc.herHim] as the dominant partner."
												:" so you can't have submissive sex with [npc.herHim].")),
									null);
							
						} else {
							return new ResponseSex("Submissive sex",
									UtilText.parse(characterForSex, "Have submissive sex with [npc.name]."), 
									Util.newArrayListOfValues(Fetish.FETISH_SUBMISSIVE), null, Fetish.FETISH_SUBMISSIVE.getAssociatedCorruptionLevel(), null, null, null,
									true, true,
									getGenericSlaveSexManager(
										Util.newArrayListOfValues(characterForSex),
										Util.newArrayListOfValues(Main.game.getPlayer()),
										(characterForSex.hasSlavePermissionSetting(SlavePermissionSetting.GENERAL_CRAWLING)
												?Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY)
												:new ArrayList<>()), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getTextFilePath(), "SEX_AS_SUB_START", characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									if(!characterForSex.isDoll()) {
										Main.game.getTextEndStringBuilder().append(characterForSex.incrementAffection(Main.game.getPlayer(), 5));
									}
									//applyImmobilisationText(Util.newArrayListOfValues(Main.game.getPlayer()));
								}
							};
						}
						
					} else if(index == 7) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Spitroasted (front)", "You'd a third person to be present in order to get spitroasted...", null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Spitroasted (front)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSex.isDollStatue()) {
							return new Response("Spitroasted (front)",
									UtilText.parse(characterForSex,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Spitroasted (front)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if (characterForSexSecondary.isDollStatue()) {
							return new Response("Spitroasted (front)",
									UtilText.parse(characterForSexSecondary,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if(!characterForSex.isAttractedTo(Main.game.getPlayer())) {
							if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
								return new Response("Spitroasted (front)",
										UtilText.parse(characterForSexSecondary, characterForSex,
												"Neither [npc.name] nor [npc2.name] are attracted to you,"
												+ (Main.game.isNonConEnabled() && characterForSexSecondary.isSlave()
														?" so if you wanted to have sex with them, you'd need to rape them as the dominant partner."
														:" so you can't have submissive sex with them.")),
										null);
							} else {
								return new Response("Spitroasted (front)",
										UtilText.parse(characterForSex,
												"[npc.Name] is not attracted to you,"
												+ (Main.game.isNonConEnabled()
													?" so if you wanted to have sex with [npc.herHim], you'd need to rape [npc.herHim] as the dominant partner."
													:" so you can't have submissive sex with [npc.herHim].")),
										null);
							}
							
						} else if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Spitroasted (front)",
									UtilText.parse(characterForSexSecondary,
										"[npc.Name] is not attracted to you,"
										+ (Main.game.isNonConEnabled() && characterForSexSecondary.isSlave()
											?" so if you wanted to have sex with [npc.herHim], you'd need to rape [npc.herHim] as the dominant partner."
											:" so you can't have submissive sex with [npc.herHim].")),
									null);
							
						} else {
							return new ResponseSex(
									"Spitroasted (front)",
									UtilText.parse(characterForSex, characterForSexSecondary, "Get down on all fours facing [npc.name], so that [npc.she] can use your mouth while [npc2.name] takes your rear."),
									null, null, null, null, null, null,
									true, true,
									getGenericSlaveSexManager(
										Util.newArrayListOfValues(characterForSexSecondary, characterForSex),
										Util.newArrayListOfValues(Main.game.getPlayer()),
										Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SPITROASTED_START", characterForSex, characterForSexSecondary)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(Main.game.getPlayer()));
								}
							};
						}
							
						
					} else if(index == 8) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Spitroast (behind)", "You'd need a third person to be present in order to get a spitroast going...", null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Spitroasted (behind)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSex.isDollStatue()) {
							return new Response("Spitroasted (behind)",
									UtilText.parse(characterForSex,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isDollStatue()) {
							return new Response("Spitroasted (behind)",
									UtilText.parse(characterForSexSecondary,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Spitroasted (behind)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if(!characterForSex.isAttractedTo(Main.game.getPlayer())) {
							if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
								return new Response("Spitroasted (behind)",
										UtilText.parse(characterForSexSecondary, characterForSex,
												"Neither [npc.name] nor [npc2.name] are attracted to you,"
												+ (Main.game.isNonConEnabled() && characterForSexSecondary.isSlave()
														?" so if you wanted to have sex with them, you'd need to rape them as the dominant partner."
														:" so you can't have submissive sex with them.")),
										null);
							} else {
								return new Response("Spitroasted (behind)",
										UtilText.parse(characterForSex,
												"[npc.Name] is not attracted to you,"
												+ (Main.game.isNonConEnabled()
													?" so if you wanted to have sex with [npc.herHim], you'd need to rape [npc.herHim] as the dominant partner."
													:" so you can't have submissive sex with [npc.herHim].")),
										null);
							}
							
						} else if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Spitroasted (behind)",
									UtilText.parse(characterForSexSecondary,
										"[npc.Name] is not attracted to you,"
										+ (Main.game.isNonConEnabled() && characterForSexSecondary.isSlave()
											?" so if you wanted to have sex with [npc.herHim], you'd need to rape [npc.herHim] as the dominant partner."
											:" so you can't have submissive sex with [npc.herHim].")),
									null);
							
						} else {
							return new ResponseSex(
									"Spitroasted (behind)",
									UtilText.parse(characterForSex, characterForSexSecondary, "Get down on all fours and present your rear to [npc.name], so that [npc.she] can fuck you while [npc2.name] uses your mouth."),
									null, null, null, null, null, null,
									true, true,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(characterForSex, characterForSexSecondary),
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SPITROASTED_START", characterForSexSecondary, characterForSex)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(Main.game.getPlayer()));
								}
							};
						}
					
					} else if(index == 9) {
						if(characterForSexSecondary==null || charactersPresent.size()<2) {
							return new Response("Side-by-side (as sub)", UtilText.parse(characterForSex, "You'd need a third person to be present in order to get fucked alongside either them or [npc.name]..."), null);
							
						} else if(characterForSex.isPlayer()) {
							return new Response("Side-by-side (as sub)", "You cannot target yourself for this action!", null);
							
						} else if (characterForSex.isDollStatue()) {
							return new Response("Side-by-side (as sub)",
									UtilText.parse(characterForSex,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isDollStatue()) {
							return new Response("Side-by-side (as sub)",
									UtilText.parse(characterForSexSecondary,
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (characterForSexSecondary.isAsleep()) {
							return new Response("Side-by-side (as sub)",
									UtilText.parse(characterForSexSecondary,
											"As [npc.sheIsFull] currently asleep, [npc.name] cannot participate in sex..."),
									null);
							
						} else if(!characterForSex.isAttractedTo(Main.game.getPlayer())) {
							if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
								return new Response("Side-by-side (as sub)", UtilText.parse(characterForSexSecondary, characterForSex, "Neither [npc.name] nor [npc2.name] are attracted to you..."), null);
							} else {
								return new Response("Side-by-side (as sub)", UtilText.parse(characterForSex, "[npc.Name] is not attracted to you, and so would be unwilling to participate in a threesome..."), null);
							}
							
						} else if(!characterForSexSecondary.isAttractedTo(Main.game.getPlayer())) {
							return new Response("Side-by-side (as sub)", UtilText.parse(characterForSexSecondary, characterForSex, "[npc.Name] is not attracted to you, and so neither [npc.she] nor [npc2.name] would be willing to have a threesome..."), null);
							
						} else if(!characterForSexSecondary.isAttractedTo(characterForSex)) {
							return new Response("Side-by-side (as sub)",
									UtilText.parse(characterForSexSecondary, characterForSex, "[npc.Name] is not attracted to [npc2.name], and so neither of them would be willing to be in a threesome position in which they are expected to interact with one other..."),
									null);

						} else if(!characterForSex.isAttractedTo(characterForSexSecondary)) {
							return new Response("Side-by-side (as sub)",
									UtilText.parse(characterForSexSecondary, characterForSex, "[npc2.Name] is not attracted to [npc.name], and so neither of them would be willing to be in a threesome position in which they are expected to interact with one other..."),
									null);
							
						} else {
							return new ResponseSex("Side-by-side (as sub)",
									UtilText.parse(characterForSex, characterForSexSecondary, "Get down on all fours beside [npc2.name], so that [npc.name] can kneel down behind the two of you, ready to fuck you both side-by-side."),
									null, null, null, null, null, null,
									true, false,
									getGenericSlaveSexManager(
											Util.newArrayListOfValues(characterForSex),
											Util.newArrayListOfValues(Main.game.getPlayer(), characterForSexSecondary),
											Util.newArrayListOfValues(ResponseTag.PREFER_DOGGY), false),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), "SEX_SIDE_BY_SIDE_AS_SUB_START", characterForSex, characterForSexSecondary)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(Util.newArrayListOfValues(Main.game.getPlayer(), characterForSexSecondary));
								}
							};
						}
					
					}else if(index==10) {
						if(charactersPresent.size()<3) {
							return new Response("Gangbang (as sub)",
									"There need to be at least three characters present in order to start a gangbang.",
									null);
							
						} else if (charactersPresent.stream().anyMatch(c->c.isDollStatue())) {
							return new Response("Gangbang (as sub)",
									UtilText.parse(charactersPresent.stream().filter(c->c.isDollStatue()).findFirst().get(),
											"As an immobile doll, [npc.name] cannot take a dominant role in sex..."),
									null);
							
						} else if (charactersPresent.stream().anyMatch(c->c.isAsleep())) {
							boolean soloSleeper = charactersPresent.stream().filter(c->c.isAsleep()).count()==1;
							String names = Util.charactersToStringListOfNames(charactersPresent.stream().filter(c->c.isAsleep()).collect(Collectors.toList()));
							return new Response("Gangbang (as sub)",
											names+" "+(soloSleeper?"needs":"need")+" to be awake to participate in a gangbang!",
									null);
							
						} else {
							boolean isRape = charactersPresent.stream().anyMatch(c->!c.isAttractedTo(Main.game.getPlayer()));
							// Handle attraction/non-con:
							if(isRape) {
								List<GameCharacter> nonAttracted = new ArrayList<>();
								for(GameCharacter character : charactersPresent) {
									if(!character.isAttractedTo(Main.game.getPlayer())) {
										nonAttracted.add(character);
									}
								}
								String isAre = nonAttracted.size()==1?"is":"are";
								String them = nonAttracted.size()==1?UtilText.parse(nonAttracted.get(0), "[npc.herHim]"):"them";
								return new Response("Gangbang (as sub)",
										"As "+Util.charactersToStringListOfNames(nonAttracted)+" "+isAre+" not attracted to you, you cannot make "+them+" dominantly participate in a gangbang you.",
										null);
							}
							
							List<GameCharacter> sexParticipants = new ArrayList<GameCharacter>(charactersPresent);
							return new ResponseSex(
									"Gangbang (as sub)",
									"Have "+Util.charactersToStringListOfNames(sexParticipants)+" take the dominant roles and get them to gangbang you.",
									null, null, null, null, null, null,
									true, false,
									getGenericSlaveSexManager(
											sexParticipants,
											Util.newArrayListOfValues(Main.game.getPlayer()),
											Util.newArrayListOfValues(),
											true),
									getAfterSexDialogue(),
									UtilText.parseFromXMLFile(getThreesomeTextFilePath(), sexParticipants.size()==3?"SEX_GANGBANG_AS_SUB_3":"SEX_GANGBANG_AS_SUB_4", sexParticipants)) {
								@Override
								public void effects() {
									applyReactionReset();
									//applyImmobilisationText(sexParticipants);
								}
							};
						}
						
					} else if(index==11) {
						if(characterForSexSecondary!=null) {
							return new ResponseEffectsOnly(
									UtilText.parse(characterForSex, "Target: <b style='color:"+characterForSex.getFemininity().getColour().toWebHexString()+";'>[npc.Name]</b>"),
									"Cycle the targeted character for group sex.") {
								@Override
								public void effects() {
									if(charactersPresent.size()>1) {
										for(int i=0; i<charactersPresent.size();i++) {
											if(charactersPresent.get(i).equals(characterForSex)) {
												if(i==charactersPresent.size()-1) {
													characterForSex = charactersPresent.get(0);
													if(characterForSexSecondary.equals(characterForSex)) {
														characterForSexSecondary = charactersPresent.get(1);
													}
												} else {
													characterForSex = charactersPresent.get(i+1);
													if(characterForSexSecondary.equals(characterForSex)) {
														characterForSexSecondary = charactersPresent.get((i+2)<charactersPresent.size()?(i+2):0);
													}
													break;
												}
											}
										}
									}
									Main.game.updateResponses();
								}
							};
							
						} else {
							return new Response(
									UtilText.parse(characterForSex, "Target: <b>[npc.Name]</b>"),
									"Cycle the targeted character for group sex.<br/>[style.italicsBad(You'd need to have a companion with you for this action to be unlocked!)]",
									null); 
						}
						
					} else if(index==12) {
						if(characterForSexSecondary!=null) {
							return new ResponseEffectsOnly(
									UtilText.parse(characterForSexSecondary, "Secondary: <b style='color:"+characterForSexSecondary.getFemininity().getColour().toWebHexString()+";'>[npc.Name]</b>"),
									"Cycle the secondary targeted character for group sex.") {
								@Override
								public void effects() {
									if(charactersPresent.size()>1) {
										for(int i=0; i<charactersPresent.size();i++) {
											if(charactersPresent.get(i).equals(characterForSexSecondary)) {
												if(i==charactersPresent.size()-1) {
													characterForSexSecondary = charactersPresent.get(0);
													if(characterForSexSecondary.equals(characterForSex)) {
														characterForSex = charactersPresent.get(1);
													}
												} else {
													characterForSexSecondary = charactersPresent.get(i+1);
													if(characterForSexSecondary.equals(characterForSex)) {
														characterForSex = charactersPresent.get((i+2)<charactersPresent.size()?(i+2):0);
													}
												}
												break;
											}
										}
									}
									Main.game.updateResponses();
								}
							};
							
						} else {
							return new Response(
									UtilText.parse(characterForSex, "Secondary: <b>[npc.Name]</b>"),
									"Cycle the secondary targeted character for group sex.<br/>[style.italicsBad(You'd need to have a companion with you for this action to be unlocked!)]",
									null);
						}
					}
				}
				return null;
				
			} else if(responseTab == 2) {
				if(isDollStatue()) {
					if(index == 1) {
						return new Response("Stop statue",
								UtilText.parse(getSlave(), "Tell [npc.name] to temporarily stop acting like a statue so that you can manage [npc.herHim]."),
								SLAVE_START_NO_CONTENT) {
							@Override
							public void effects() {
								dollStatueInterrupted = true;
								Main.game.appendToTextEndStringBuilder("<p>");
									Main.game.appendToTextEndStringBuilder("Wanting to interact with [npc.name] without [npc.herHim] continuing to act like an unresponsive statue, you give [npc.herHim] permission to temporarily move freely once again.");
								Main.game.appendToTextEndStringBuilder("</p>");
								Main.game.appendToTextEndStringBuilder(getSlaveStartCoreContent());
							}
						};
						
					} else if(index==0) {
						return CompanionManagement.getManagementResponses(index);
					}
					return null;
					
				} else if(getSlave().isAsleep()) {
					if(index == 1) {
						return getWakeUpResponse();
						
					} else if(index==0) {
						return CompanionManagement.getManagementResponses(index);
					}
					return null;

				} else {
					return CompanionManagement.getManagementResponses(index);
				}
			
			} else {
				return null;
			}
		}
	};
	
	private static String getFooterText() {
		if(isDoll()) {
			return "";
		}
		return "<p><i>"
				+ (getSlave().isAttractedTo(Main.game.getPlayer())
						?"From the way [npc.she] keeps on glancing hungrily at your body, you can tell that [npc.sheIs] attracted to you..."
						:"[npc.She] doesn't seem to be attracted to you...")
					+ "</i></p>";
	}
	
	public static final DialogueNode SLAVE_PROGRESSION = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Talking with [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);
			
			if(isDoll()) {
				UtilText.nodeContentSB.append(
						"<p>"
							+ "Wanting to hear about it from [npc.her] point of view, you ask [npc.name] what [npc.she] can tell you about the process by which [npc.she] was created."
						+ "</p>"
						+ "<p>"
							+ "[npc.speech(I'm sorry, [pc.name], but I don't remember anything of it,)] [npc.she] replies, [npc.speech(My first memory is of being commanded to take my place alongside a dozen other dolls, and to there wait until I was sold.)]"
						+ "</p>"
						+ "<p>"
							+ "It doesn't sound like [npc.name] is going to be able to tell you anything you didn't already learn from Saellatrix, and so decide not to waste your time with any more questions on this subject..."
						+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append(
						"<p>"
							+ "<i>This isn't fully implemented yet!</i>" //TODO
						+ "</p>"
						+ "<p>"
							+ "Deciding that you'd like to talk to [npc.name] about something a little more serious, you ask [npc.herHim] about [npc.her] life before becoming your slave,"
							+ " [pc.speech(I'd like to know a little more about your past, [npc.name]. What was your life like before coming here?)]"
						+ "</p>"
						+ "<p>");
				
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append("With a look of intense hatred in [npc.her] [npc.eyes], [npc.she] quickly spits out an insolent response,"
										+ " [npc.speech(Fuck off! Like I'm going to talk about that stuff with you! Asshole!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append("Although [npc.she] tries to conceal it, you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] remarks,"
										+ " [npc.speech(I wasn't doing much. There's really nothing more to say, [npc.pcName].)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append("[npc.She] obediently does [npc.her] very best to conceal the look of hatred in [npc.her] [npc.eyes] as [npc.she] responds,"
										+ " [npc.speech(There's not really much to say about all that, [npc.pcName]. I lived an uneventful life up until becoming your property. Is there anything else you need?)]");
								break;
						}
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append("Although [npc.she] doesn't seem to hate you, [npc.name] obviously doesn't feel too comfortable talking about [npc.her] past with you, and sighs,"
										+ " [npc.speech(I don't know, [npc.pcName], it's not like there's anything to tell, really. Let's just talk about something else, ok?)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append("Although [npc.she] doesn't seem to hate you, [npc.name] obviously doesn't feel too comfortable talking about [npc.her] past with you, and sighs,"
										+ " [npc.speech(I'm sorry [npc.pcName], there's not really anything to say about my past. Perhaps I can do something else for you?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append("Although [npc.she] doesn't seem to hate you, [npc.name] obviously doesn't feel too comfortable talking about [npc.her] past with you, and simply responds,"
										+ " [npc.speech(There isn't anything to say about that, [npc.pcName]. My life was entirely uneventful before becoming your slave. Can I do anything else for you?)]");
								break;
						}
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) { //TODO
							case DISOBEDIENT:
								UtilText.nodeContentSB.append("Barely able to contain [npc.her] excitement at being asked about [npc.her] past life, [npc.name] quickly responds,"
										+ " [npc.speech(Thanks for asking, [npc.pcName]! Oh, but maybe we should talk about this some other time...)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append("[npc.Name] smiles as you ask [npc.herHim] about [npc.her] past life, and responds,"
										+ " [npc.speech(Ah, [npc.pcName], maybe we should talk about this some other time...)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append("Clearly happy at being asked about [npc.her] past life, [npc.name] quickly responds,"
										+ " [npc.speech(I'm sorry, [npc.pcName], but we'll have to talk about this some other time...)]");
								break;
						}
						break;
				}
				UtilText.nodeContentSB.append("</p>");
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}
		
		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_MINOR = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Talking with [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);
			
			if(isDoll()) {
				UtilText.nodeContentSB.append("<p>"
							+ "Deciding to take advantage of the fact that [npc.name] is more than just a sex doll, you start to make small talk with [npc.herHim]."
						+ "</p>"
						+ "<p>"
							+ "At first it seems like you're talking to a normal person, as [npc.name] responds to your questions and statements in an engaging manner."
							+ " You soon notice, however, that [npc.she] doesn't really offer anything to the conversation, and is simply reacting to whatever you say."
						+ "</p>"
						+ "<p>"
							+ "Having noticed this limitation of [npc.namePos] abilities, you allow the conversation to end, before wondering what to do next..."
						+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append("<p>"
							+ "You decide to try and make some small talk with [npc.name], and ask [npc.her] a series of questions ranging from how [npc.sheIs] finding life as your slave,"
							+ " to what [npc.she] thinks of the peculiar arcane weather here in Dominion.");
				
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(" No matter how hard you try, however, your attempts at conversation are met with incredibly rude remarks."
										+ "</p>"
										+ "<p>"
										+ "Realising that you're not going to get anywhere like this, you give up on trying to talk to [npc.name]."
										+ " As you turn away, [npc.she] scowls."
										+ " [npc.speech(Can you please just fuck off now?!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(" No matter how hard you try, however, your attempts at conversation are met with short, dismissive remarks."
										+ "</p>"
										+ "<p>"
										+ "Realising that you're not going to get anywhere like this, you give up on trying to talk to [npc.name]."
										+ " As you turn away, [npc.she] scowls."
										+ " [npc.speech(Are you finished with me, [npc.pcName]?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(" No matter how hard you try, however, your attempts at conversation are met with short, dismissive remarks."
										+ "</p>"
										+ "<p>"
										+ "Realising that you're not going to get anywhere like this, you give up on trying to talk to [npc.name]."
										+ " As you turn away, [npc.she] asks,"
										+ " [npc.speech(Is there anything else I can do for you, [npc.pcName]?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, your attempt at making small talk is doing more harm than good!</i>"
								+ "</p>");
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											" Although [npc.she] seems uninterested in talking to you at first, [npc.name] nevertheless responds to each of your questions in an amicable manner."
											+ " From the smile that slowly forms on [npc.her] face, you can tell that [npc.she] appreciates the fact that you're attempting to put [npc.herHim] at ease."
										+ "</p>"
										+ "<p>"
											+ "After spending some time talking with [npc.name] like this, you decide to bring your conversation to an end, and as you do, your slave mutters,"
											+ " [npc.speech(Thanks for talking to me, [npc.pcName]...)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										" Although [npc.she] seems a little hesitant to talk to you, [npc.name] nevertheless responds to each of your questions in an amicable manner."
										+ " From the smile that slowly forms on [npc.her] face, you can tell that [npc.she] appreciates the fact that you're attempting to put [npc.herHim] at ease."
									+ "</p>"
									+ "<p>"
										+ "After spending some time talking with [npc.name] like this, you decide to bring your conversation to an end, and as you do, your slave mutters,"
										+ " [npc.speech(Thank you, [npc.pcName]. I enjoyed talking with you...)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										" [npc.She] obediently responds to each one of your questions in an amicable manner."
										+ " From the smile that slowly forms on [npc.her] face, you can tell that [npc.she] appreciates the fact that you're attempting to put [npc.herHim] at ease."
									+ "</p>"
									+ "<p>"
										+ "After spending some time talking with [npc.name] like this, you decide to bring your conversation to an end, and as you do, your slave smiles."
										+ " [npc.speech(I hope my answers were to your satisfaction, [npc.pcName].)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, your attempt at making small talk is helping to get [npc.herHim] to like you more!</i>"
								+ "</p>");
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											" Beaming from ear to ear, [npc.name] enthusiastically responds to each of your questions, [npc.her] attitude more like that of a close friend than of your slave."
											+ " From [npc.her] smile and the way [npc.she] looks longingly up into your [pc.eyes], you can tell that [npc.name] really appreciates the fact that you're taking some time to talk with [npc.herHim]."
										+ "</p>"
										+ "<p>"
											+ "After a little while, you decide to bring your conversation to an end, and as you do, your slave grins at you."
											+ " [npc.speech(Thanks, [npc.pcName]! It's really great getting to talk with you now and again!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										" Beaming from ear to ear, [npc.name] enthusiastically responds to each of your questions, but [npc.she] makes sure not to overdo it, remaining a little distant as [npc.she] tries to act like a good slave."
										+ " From [npc.her] smile and the way [npc.she] looks longingly up into your [pc.eyes], you can tell that [npc.name] really appreciates the fact that you're taking some time to talk with [npc.herHim]."
									+ "</p>"
									+ "<p>"
										+ "After a little while, you decide to bring your conversation to an end, and as you do, your slave smiles."
										+ " [npc.speech(Thank you, [npc.pcName]. I appreciate you taking your time to talk with me.)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										" Trying to control the excitement in [npc.her] voice, [npc.name] enthusiastically responds to each of your questions,"
												+ " although [npc.sheIs] careful to retain [npc.her] composure, doing [npc.her] very best to act like an obedient slave."
										+ " [npc.She] can't manage to totally suppress [npc.her] smile and the way [npc.she] looks longingly up into your [pc.eyes], however,"
											+ " letting you know that [npc.she] really appreciates the fact that you're taking some time to talk with [npc.herHim]."
									+ "</p>"
									+ "<p>"
										+ "After a little while, you decide to bring your conversation to an end, and as you do, your slave smiles."
										+ " [npc.speech(I hope my answers were to your satisfaction, [npc.pcName].)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, your attempt at making small talk is greatly helping to get [npc.herHim] to like you more!</i>"
								+ "</p>");
						break;
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_ENCOURAGE = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Encouraging [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			if(isDoll()) {
				String jobName = getCurrentJob().getName(getSlave());
				UtilText.nodeContentSB.append("<p>"
							+ "Wanting to hear what [npc.name] has to say about [npc.her] job, you ask [npc.herHim] what [npc.she] thinks of working as "
							+ UtilText.generateSingularDeterminer(jobName)+" "+jobName+"."
						+ "</p>"
						+ "<p>"
							+ "[npc.speech(I do not think anything of it, [pc.name],)] the doll responds, [npc.speech(I am simply here to serve you, no matter what it is that you ask me to do.)]"
						+ "</p>"
						+ "<p>"
							+ "It doesn't seem like you're going to get much out of [npc.name] on this topic..."
						+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append("<p>");
				
				switch(getCurrentJob()) {
					case CLEANING:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as your ");
						UtilText.nodeContentSB.append(Util.capitaliseSentence(SlaveJob.CLEANING.getName(getSlave())));
						UtilText.nodeContentSB.append(", you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case IDLE:
						UtilText.nodeContentSB.append("Although [npc.name] hasn't been assigned to a job, you ask [npc.her] how [npc.sheIs] finding life as your slave.");
						break;
					case GARDEN:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as a gardener, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case SECURITY:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as a security guard, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case KITCHEN:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working in the kitchen as your cook, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case LAB_ASSISTANT:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working in Lilaya's lab as your [lilaya.relation(pc)]'s assistant, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case LIBRARY:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as a librarian in Lilaya's extensive library, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case OFFICE:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working in the office, you ask [npc.her] if [npc.sheIs] happy dealing with all the paperwork.");
						break;
					case TEST_SUBJECT:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while being used as a test subject for Lilaya's work on transformations, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case PUBLIC_STOCKS:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while being publicly used in the stocks in Slaver Alley, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case PROSTITUTE:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as a prostitute in Angel's Kiss, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case MILKING:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working in the milking stalls, you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case BEDROOM:
						UtilText.nodeContentSB.append("Wanting to make sure that [npc.nameIsFull] happy, you ask [npc.herHim] how [npc.she] feels about being assigned to wait upon you in your bedroom.");
						break;
					case SPA:
						UtilText.nodeContentSB.append("Wanting to make sure that [npc.nameIsFull] happy, you ask [npc.herHim] how [npc.she] feels about being assigned to work in the spa.");
						break;
					case SPA_RECEPTIONIST:
						UtilText.nodeContentSB.append("Wanting to make sure that [npc.nameIsFull] happy, you ask [npc.herHim] how [npc.she] feels about being assigned to work in the spa.");
						break;
					case DINING_HALL:
						UtilText.nodeContentSB.append("Wanting to encourage [npc.name] to do [npc.her] best while working as your ");
						UtilText.nodeContentSB.append(Util.capitaliseSentence(SlaveJob.DINING_HALL.getName(getSlave())));
						UtilText.nodeContentSB.append(", you ask [npc.her] how [npc.sheIs] finding it.");
						break;
					case DOLL_STATUE:
						break;
				}
				
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(" The moment that you finish speaking, your disobedient slave spits out,"
										+ " [npc.speech(Fuck off! I hate it, and I'm not even doing any work while I'm on duty, so fuck you!)]"
										+ "</p>"
										+ "<p>"
										+ "From [npc.her] rude reaction, it's quite clear that [npc.name] not only hates you, but also isn't too keen on living life as your slave."
										+ " Before you can try asking [npc.herHim] to give you a proper answer, [npc.she] turns [npc.her] back on you and snarls,"
										+ " [npc.speech(Why don't you fuck off and go suck Lilaya's cock!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(" The moment that you finish speaking, your slave sharply responds,"
										+ " [npc.speech(I do what I must. I don't enjoy it, or being your slave for that matter, but I'll do what I have to.)]"
										+ "</p>"
										+ "<p>"
										+ "From [npc.her] curt reaction, it's quite clear that [npc.name] isn't too well adjusted to [npc.her] life as a slave."
										+ " Before you can say anything else, [npc.she] impatiently asks,"
										+ " [npc.speech(Is there anything else, [npc.pcName]? Or are you finished with me for now?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(" The moment that you finish speaking, your obedient slave quickly responds,"
										+ " [npc.speech(I do whatever I'm told to, [npc.pcName]. I'll carry out my duties to the best of my ability, as that's what's expected of me.)]"
										+ "</p>"
										+ "<p>"
										+ "From [npc.her] curt reaction, it's quite clear that while [npc.name] will obediently carry out [npc.her] duties as a slave, [npc.she] doesn't like you."
										+ " Before you can say anything else, [npc.she] asks,"
										+ " [npc.speech(What more do you need of me, [npc.pcName]?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, showing your interest in [npc.her] work has only had a minor effect!</i>"
								+ "</p>");
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(" The moment that you finish speaking, your disobedient slave whines,"
										+ " [npc.speech(Well, it's not like I have a choice in the matter. I mean, thanks for asking, but I'm a slave, so I've kind of got to do whatever you order me to, whether I like it or not...)]"
										+ "</p>"
										+ "<p>"
										+ "From [npc.her] brutally honest reaction, it's quite clear that [npc.name] isn't fully adjusted to living life as your slave just yet."
										+ " As you wonder how to respond, [npc.she] asks,"
										+ " [npc.speech(So is there anything you want, [npc.pcName]?)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(" The moment that you finish speaking, your slave replies,"
										+ " [npc.speech(Well, it's not too bad, [npc.pcName]. Thanks for asking, I'll do whatever you order me to...)]"
										+ "</p>"
										+ "<p>"
										+ "From [npc.her] honest reaction, it's quite clear that [npc.name] still has a few reservations about being your slave."
										+ " As you wonder how to respond, [npc.she] asks,"
										+ " [npc.speech(Is there anything else I can do for you, [npc.pcName]?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
											" The moment that you finish speaking, your slave replies,"
											+ " [npc.speech(I always do my best, [npc.pcName]. Thank you for asking, but please be assured that I'm working to the best of my abilities.)]"
										+ "</p>"
										+ "<p>"
											+ "From [npc.her] reaction, it's quite clear that [npc.name] has fully accepted [npc.her] place as your slave."
											+ " [npc.Her] answer seemed a little cold, however, and you realise that while [npc.she] doesn't hate you, [npc.she] doesn't exactly love you either."
											+ " Before you can make a comment, [npc.she] continues,"
											+ " [npc.speech(What more can I do for you, [npc.pcName]?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, showing your interest in [npc.her] work has had a noticeable effect!</i>"
								+ "</p>");
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											" The moment that you finish speaking, your disobedient slave laughs,"
											+ " [npc.speech(Well, it's not like I really have a choice in the matter, do I? Haha! Don't worry though, I love you! I-I mean! I love <i>working</i> for you!)]"
										+ "</p>"
										+ "<p>"
											+ "[npc.Her] cheeks flush bright red as [npc.she] tries to hastily cover up [npc.her] slip of the tongue, but even without [npc.her] accidental confession, you already know that [npc.name] cares for you deeply."
											+ " From the way [npc.she] looks at you, to the smile that breaks out on [npc.her] face each time you show [npc.herHim] any attention, it's clear to everyone how infatuated with you [npc.she] is."
											+ " Before you can say anything on the matter, your slave quickly tries to shift the topic to something less embarrassing for [npc.herHim],"
											+ " [npc.speech(Can I do anything else for you, [npc.pcName]? Anything at all, just ask!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										" The moment that you finish speaking, your slave smiles and responds,"
										+ " [npc.speech(Everything's going fine, thank you for asking, [npc.pcName]. I love working for you...)]"
									+ "</p>"
									+ "<p>"
										+ "[npc.Her] cheeks flush bright red as [npc.she] admits to [npc.her] enjoyment at being your slave, but even without [npc.her] shy display, you already know that [npc.name] cares for you deeply."
										+ " From the way [npc.she] looks at you, to the smile that breaks out on [npc.her] face each time you show [npc.herHim] any attention, it's clear to everyone how infatuated with you [npc.she] is."
										+ " Before you can say anything on the matter, your slave quickly tries to shift the topic to something less embarrassing for [npc.herHim],"
										+ " [npc.speech(Can I do anything else for you, [npc.pcName]?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										" The moment that you finish speaking, your slave smiles and responds,"
										+ " [npc.speech(Everything's going very well, [npc.pcName]. I love working for you...)]"
									+ "</p>"
									+ "<p>"
										+ "[npc.Her] cheeks flush bright red as [npc.she] admits to [npc.her] enjoyment at being your slave, but even without [npc.her] shy display, you already know that [npc.name] cares for you deeply."
										+ " From the way [npc.she] looks at you, to the smile that breaks out on [npc.her] face each time you show [npc.herHim] any attention, it's clear to everyone how infatuated with you [npc.she] is."
										+ " Before you can say anything on the matter, your slave quickly tries to shift the topic to something less embarrassing for [npc.herHim],"
										+ " [npc.speech(Can I do anything else for you, [npc.pcName]?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, showing your interest in [npc.her] work has had a significant effect!</i>"
								+ "</p>");
						break;
				}
			}
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_HUG = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Hugging [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			if(isDoll()) {
				UtilText.nodeContentSB.append("<p>"
							+ "Wanting to give [npc.name] some physical affection, you [pc.step] up to [npc.herHim] and pull [npc.herHim] into a big hug."
						+ "</p>"
						+ "<p>"
							+ "Your doll immediately responds in kind by wrapping [npc.her] [npc.arms] around you and pulling you into [npc.herHim]."
							+ " Although it's the reaction you wanted, [npc.namePos] hug doesn't have any warmth or intimacy to it, and so you soon break it off and [pc.step] back from [npc.herHim]."
						+ "</p>"
						+ "<p>"
							+ "[npc.speech(I hope that was to your satisfaction, [pc.name],)] [npc.she] says, before waiting to hear what it is you want of [npc.herHim] next..."
						+ "</p>");
				
			} else {
				AffectionLevelBasic slaveAffection = AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()));
				
				boolean silly = Main.game.isSillyMode() && Math.random()<0.25f && slaveAffection==AffectionLevelBasic.NEUTRAL;
				
				UtilText.nodeContentSB.append("<p>");
				
				if(silly) {
					UtilText.nodeContentSB.append("Wanting [npc.name] to show you some physical affection, you ask [npc.herHim] to give you a hug.");
				} else {
					UtilText.nodeContentSB.append("You decide that [npc.name] is in need of some physical comfort, and, stepping forwards, you reach and take hold of [npc.herHim], before pulling [npc.herHim] into a tight hug.");
				}
				
				switch(slaveAffection) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											" [npc.She] instantly tries to wriggle free from your grasp, shouting,"
											+ " [npc.speech(Let me go! Fuck off already!)]"
										+ "</p>"
										+ "<p>"
											+ "You ignore your disobedient slave's protests, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
											+ " [npc.She] carries on struggling against you, not at all impressed by your method of reassuring [npc.herHim]."
											+ " After a short while, you finally release [npc.name], and [npc.she] staggers back, shouting,"
											+ " [npc.speech(I don't need your sympathy, you "+(Main.game.getPlayer().isFeminine()?"bitch":"bastard")+"!)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										" [npc.She] instinctively tries to pull free from your grasp, whining,"
										+ " [npc.speech([npc.PcName], please! I don't want this!)]"
									+ "</p>"
									+ "<p>"
										+ "You ignore your slave's protests, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
										+ " [npc.She] tries to slip free from your grip, clearly not impressed by your method of reassuring [npc.herHim], but you make sure to hold on tightly, preventing [npc.her] escape."
										+ " After a short while, you finally release [npc.name], and [npc.she] staggers back, muttering,"
										+ " [npc.speech(I don't even like hugs...)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										" [npc.She] freezes up as you take hold of [npc.herHim], and mutters, without a trace of sincerity in [npc.her] voice,"
										+ " [npc.speech(Thank you, [npc.pcName]...)]"
									+ "</p>"
									+ "<p>"
										+ "You ignore your slave's cold reaction, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
										+ " [npc.She] remains completely still, clearly not impressed by your method of reassuring [npc.herHim], but you ignore [npc.her] refusal to react, and continue pressing yourself against [npc.herHim]."
										+ " After a short while, you finally release [npc.name], and [npc.she] steps back, looking down at the floor."
										+ " [npc.speech(Thank you, [npc.pcName]. What else do you require?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, your attempt at forcing physical contact is doing more harm than good!</i>"
								+ "</p>");
						break;
					case NEUTRAL:
						if(silly) {
							UtilText.nodeContentSB.append(
									"</p>"
									+ "<p>"
										+ "[npc.speech(~Eugh!~ Fine...)] [npc.name] relents, before blushing and holding out [npc.her] [npc.arms] towards you."
										+ " Beckoning you forwards into [npc.her] embrace, she sighs, [npc.speech(I guess you are my little pogchamp... Come here...)]"
									+ "</p>"
									+ "<p>"
										+ "[pc.Stepping] forwards, you ignore [npc.namePos] embarrassment and let [npc.herHim] give you a big hug."
										+ " [npc.She] pats your back a little, and you get the distinct impression that [npc.sheIs] only reciprocating your gesture because that's what's expected of [npc.herHim]."
										+ " After a short while, you finally release [npc.name], allowing [npc.herHim] to [npc.step] back and mutter,"
										+ " [npc.speech(That was kind of poggers...)]");
						} else {
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
											" [npc.She] half-heartedly returns your embrace, sighing,"
											+ " [npc.speech(Thanks, [npc.pcName], I guess a hug now and then isn't too bad...)]"
										+ "</p>"
										+ "<p>"
											+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
											+ " [npc.She] pats your back a little, and you get the distinct impression that [npc.sheIs] only reciprocating your gesture because that's what's expected of [npc.herHim]."
											+ " After a short while, you finally release [npc.name], and [npc.she] steps back, smiling."
											+ " [npc.speech(That was nice, I guess. Anything else you want?)]");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											" [npc.She] half-heartedly returns your embrace, sighing,"
											+ " [npc.speech(Thank you, [npc.pcName]...)]"
										+ "</p>"
										+ "<p>"
											+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
											+ " [npc.She] pats your back a little, and you get the distinct impression that [npc.sheIs] only reciprocating your gesture because that's what's expected of [npc.herHim]."
											+ " After a short while, you finally release [npc.name], and [npc.she] steps back, smiling."
											+ " [npc.speech(Is there anything else you need, [npc.pcName]?)]");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											" [npc.She] half-heartedly returns your embrace, sighing,"
											+ " [npc.speech(Thank you, [npc.pcName].)]"
										+ "</p>"
										+ "<p>"
											+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
											+ " [npc.She] pats your back a little, and you get the distinct impression that [npc.sheIs] only reciprocating your gesture because it's what's expected of [npc.herHim]."
											+ " After a short while, you finally release [npc.name], and [npc.she] steps back, smiling."
											+ " [npc.speech(Is there anything else you need, [npc.pcName]?)]");
									break;
							}
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, giving [npc.herHim] a reassuring hug has had a positive affect on how [npc.she] sees you! However, being treated in such a familiar manner has had a slightly negative impact on [npc.her] obedience...</i>"
								+ "</p>");
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											" [npc.She] eagerly returns your embrace, letting out a deep sigh,"
											+ " [npc.speech(Thank you, [npc.pcName]!)]"
										+ "</p>"
										+ "<p>"
											+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
											+ " [npc.She] snuggles into you, wrapping [npc.her] [npc.arms] around your back and letting out a contented sigh."
											+ " From [npc.her] reaction, it's quite clear that [npc.she] really appreciates your physical gesture, and, encouraged by [npc.her] enthusiasm, you spend quite some time hugging your slave."
										+ "</p>"
										+ "<p>"
											+ "After a while, you finally release [npc.name], and [npc.she] steps back, smiling."
											+ " [npc.speech(Thank you, [npc.pcName]! I really needed that...)]");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										" [npc.She] eagerly returns your embrace, letting out a deep sigh,"
										+ " [npc.speech(Thank you, [npc.pcName]...)]"
									+ "</p>"
									+ "<p>"
										+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
										+ " [npc.She] snuggles into you, wrapping [npc.her] [npc.arms] around your back and letting out a contented sigh."
										+ " From [npc.her] reaction, it's quite clear that [npc.she] really appreciates your physical gesture, and, encouraged by [npc.her] enthusiasm, you spend quite some time hugging your slave."
									+ "</p>"
									+ "<p>"
										+ "After a while, you finally release [npc.name], and [npc.she] steps back, smiling."
										+ " [npc.speech(Thank you, [npc.pcName]. I really needed that... Is there anything I can do for you?)]");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										" [npc.She] eagerly returns your embrace, letting out a deep sigh,"
										+ " [npc.speech(Thank you, [npc.pcName]...)]"
									+ "</p>"
									+ "<p>"
										+ "You pull [npc.name] in a little more, holding [npc.herHim] close to your body and breathing in [npc.her] [npc.scent]."
										+ " [npc.She] snuggles into you, wrapping [npc.her] [npc.arms] around your back and letting out a contented sigh."
										+ " From [npc.her] reaction, it's quite clear that [npc.she] really appreciates your physical gesture, and, encouraged by [npc.her] enthusiasm, you spend quite some time hugging your slave."
									+ "</p>"
									+ "<p>"
										+ "After a while, you finally release [npc.name], and [npc.she] steps back, smiling."
										+ " [npc.speech(Thank you, [npc.pcName]. Is there anything I can do for you?)]");
								break;
						}
						UtilText.nodeContentSB.append("</p>"
								+ "<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, giving [npc.herHim] a reassuring hug has had a hugely positive affect on how [npc.she] sees you! However, being treated in such a familiar manner has had a slightly negative impact on [npc.her] obedience...</i>"
								+ "</p>");
						break;
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_PETTINGS = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Petting [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			if(isDoll()) {
				UtilText.nodeContentSB.append("<p>"
						+ "Wanting to give [npc.name] some reassuring pettings, you reach up and place a [pc.hand] on [npc.her] head."
						+ " Before [npc.she] can react, you start stroking [npc.her] [npc.hair+], before moving down to gently scratch behind one of [npc.her] [npc.ears]."
					+ "</p>"
					+ "<p>"
						+ "Your doll leans into your pettings, saying as [npc.she] does so, [npc.speech(Thank you, [pc.name]. I hope that I'm serving you well.)]"
					+ "</p>"
					+ "<p>"
						+ "Although you continue to pet [npc.name] for a few more moments, [npc.her] cold, robotic response isn't quite what you were looking for."
						+ " A blank, expressionless stare from your doll is the final straw, and you take your [pc.hand] away before wondering what to do next..."
					+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append("<p>"
								+ "Wanting to give [npc.name] some reassuring pettings, you reach up and place a [pc.hand] on [npc.her] head."
								+ " Before [npc.she] can react, you start stroking [npc.her] [npc.hair+], before moving down to gently scratch behind one of [npc.her] [npc.ears]."
							+ "</p>"
							+ "<p>");
				
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											"Shocked by your actions, it takes [npc.name] a moment to respond, and with an angry cry, [npc.she] slaps your [pc.hand] away and steps back, shouting,"
											+ " [npc.speech(What the fuck?! Get off me! Leave me alone you fuck!)]"
										+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										"Shocked by your actions, it takes [npc.name] a moment to respond, and with an uncomfortable whine, [npc.she] steps back, apologising,"
										+ " [npc.speech(Sorry, [npc.pcName], but, could you not do that?)]"
									+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										"Shocked by your actions, it takes [npc.name] a moment to respond, and with an uncomfortable whine,"
											+ " [npc.she] remains still, scrunching up [npc.her] [npc.eyes] as [npc.she] forces [npc.herself] to endure your unwanted attention."
										+ " After a moment, you stop what you're doing and take your [pc.hand] away, causing [npc.name] to let out a relieved sigh, before asking,"
										+ " [npc.speech(Is there anything else I can do for you, [npc.pcName]?)]"
									+ "</p>");
								break;
						}
						UtilText.nodeContentSB.append(
								"<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, your attempt at forcing physical contact is doing more harm than good!</i>"
								+ "</p>");
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											"Taken by surprise at your action, it takes [npc.name] a moment to respond, and with little sigh, [npc.she] tilts [npc.her] head to one side."
											+ " [npc.speech(That feels kind of good... Keep going!)]"
										+ "</p>"
										+ "<p>"
											+ "You do as [npc.she] asks, and continue stroking and patting [npc.her] head for quite some time."
											+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
										+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										"Taken by surprise at your action, it takes [npc.name] a moment to respond, and with little sigh, [npc.she] tilts [npc.her] head to one side."
										+ " [npc.speech(That feels kind of good... Thank you, [npc.pcName]...)]"
									+ "</p>"
									+ "<p>"
										+ "Encouraged by [npc.her] reaction, you continue stroking and patting [npc.her] head for quite some time."
										+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
									+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										"Taken by surprise at your action, it takes [npc.name] a moment to respond, and with little sigh, [npc.she] tilts [npc.her] head to one side."
										+ " [npc.speech(Thank you, [npc.pcName]...)]"
									+ "</p>"
									+ "<p>"
										+ "Encouraged by [npc.her] reaction, you continue stroking and patting [npc.her] head for quite some time."
										+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
									+ "</p>");
								break;
						}
						UtilText.nodeContentSB.append(
								"<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, giving [npc.herHim] such intimate physical attention has made [npc.her] like you a lot more! However, being treated in such a familiar manner has had a slightly negative impact on [npc.her] obedience...</i>"
								+ "</p>");
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											"[npc.Name] lets out a loving sigh, and steps towards you as [npc.she] tilts [npc.her] head to one side."
											+ " [npc.speech(That feels so good... Keep on going!)]"
										+ "</p>"
										+ "<p>"
											+ "You do as [npc.she] asks, and continue stroking and patting [npc.her] head for quite some time."
											+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
										+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
											"[npc.Name] lets out a loving sigh, and steps towards you as [npc.she] tilts [npc.her] head to one side."
											+ " [npc.speech(Thank you, [npc.pcName]! That feels so good! Please don't stop!)]"
										+ "</p>"
										+ "<p>"
											+ "You do as [npc.she] asks, and continue stroking and patting [npc.her] head for quite some time."
											+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
										+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
											"[npc.Name] lets out a loving sigh, and steps towards you as [npc.she] tilts [npc.her] head to one side."
											+ " [npc.speech(Thank you, [npc.pcName]!)]"
										+ "</p>"
										+ "<p>"
											+ "Encouraged by [npc.her] reaction, you continue stroking and patting [npc.her] head for quite some time."
											+ " Eventually, however, you feel as though [npc.namePos] had enough for now, and take your [pc.hand] away, smiling as your slave lets out a very contented sigh."
										+ "</p>");
								break;
						}
						UtilText.nodeContentSB.append(
								"<p>"
									+ "<i>Due to the fact that [npc.name]"
										+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
										+ " you, giving [npc.herHim] such intimate physical attention has made [npc.her] like you a lot more! However, being treated in such a familiar manner has had a slightly negative impact on [npc.her] obedience...</i>"
								+ "</p>");
						break;
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_PRESENT = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Giving [npc.Name] a present");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append("<p>"
							+ "Deciding that [npc.name] deserves a gift this Yuletide, you hold out the present towards [npc.herHim]."
							+ " [pc.speech(This is for you, [npc.name]. Happy Yuletide!)]"
						+ "</p>"
						+ "<p>");
			
			switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
				case DISLIKE:
					switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
						case DISOBEDIENT:
							UtilText.nodeContentSB.append(
										"Taken completely off-guard, [npc.name] doesn't seem to know how to respond, and as you press the gift into [npc.her] [npc.hands], [npc.she] mutters,"
										+ " [npc.speech(F-For me? I still hate you, you know...)]"
									+ "</p>");
							break;
						case NEUTRAL:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] doesn't seem to know how to respond, and as you press the gift into [npc.her] [npc.hands], [npc.she] mutters,"
									+ " [npc.speech(F-For me? I guess I'll take it then...)]"
								+ "</p>");
							break;
						case OBEDIENT:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] doesn't seem to know how to respond, but as you press the gift into [npc.her] [npc.hands], [npc.she] regains [npc.her] composure and coldly states,"
									+ " [npc.speech(I will accept this if it your wish, [npc.pcName].)]"
								+ "</p>");
							break;
					}
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>Despite the fact that [npc.she]"
									+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
									+ " you, [npc.name] is happy to have been given a gift!</i>"
							+ "</p>");
					break;
				case NEUTRAL:
					switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
						case DISOBEDIENT:
							UtilText.nodeContentSB.append(
										"Taken completely off-guard, [npc.name] lets out a happy little cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] smiles at you."
										+ " [npc.speech(F-For me? Thank you, [npc.pcName]!)]"
									+ "</p>");
							break;
						case NEUTRAL:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] lets out a happy little cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] smiles at you."
									+ " [npc.speech(F-For me? Thank you, [npc.pcName]!)]"
								+ "</p>");
							break;
						case OBEDIENT:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] lets out a happy little cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] smiles at you."
									+ " [npc.speech(Thank you, [npc.pcName], I wish you a happy Yuletide as well!)]"
								+ "</p>");
							break;
					}
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>While [npc.name] is happy to have been given a gift, being treated in such a friendly manner has had a slightly negative impact on [npc.her] obedience...</i>"
							+ "</p>");
					break;
				case LIKE:
					switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
						case DISOBEDIENT:
							UtilText.nodeContentSB.append(
										"Taken completely off-guard, [npc.name] lets out an ecstatic cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] bursts out,"
										+ " [npc.speech([npc.PcName]! Thank you so much! Happy Yuletide to you too!)]"
									+ "</p>");
							break;
						case NEUTRAL:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] lets out an ecstatic cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] bursts out,"
									+ " [npc.speech([npc.PcName]! Thank you so much! Happy Yuletide to you too!)]"
								+ "</p>");
							break;
						case OBEDIENT:
							UtilText.nodeContentSB.append(
									"Taken completely off-guard, [npc.name] lets out an ecstatic cry, and as you press the gift into [npc.her] [npc.hands], [npc.she] beams at you."
									+ " [npc.speech(Thank you, [npc.pcName]! Happy Yuletide to you too!)]"
								+ "</p>");
							break;
					}
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>While [npc.name] is happy to have been given a gift, being treated in such a friendly manner has had a slightly negative impact on [npc.her] obedience...</i>"
							+ "</p>");
					break;
			}
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	private static boolean isSlaveNaked() {
		return getSlave().isCoverableAreaVisible(CoverableArea.ANUS)
				&& getSlave().isCoverableAreaVisible(CoverableArea.NIPPLES)
				&& getSlave().isCoverableAreaVisible(CoverableArea.PENIS)
				&& getSlave().isCoverableAreaVisible(CoverableArea.VAGINA);
	}
	
	public static final DialogueNode SLAVE_INSPECT = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Inspecting [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			UtilText.nodeContentSB.append(
						"<p>"
							+ (isSlaveNaked()
									?"Deciding that you'd like to inspect [npc.namePos] naked body, you order [npc.herHim] to take a step back so that you can get a better view of [npc.herHim]."
									:"Deciding that you'd like to inspect [npc.namePos] body, you order [npc.herHim] to strip naked, before taking a step back in order to get a better view of [npc.herHim].")
						+ "</p>"
						+ "<p>");
			String legsSpreading = getSlave().hasLegs()
					?"spread [npc.her] [npc.legs] and present [npc.her] "
					:"present you with [npc.her] ";
			
			if(isDoll()) {
				UtilText.nodeContentSB.append(
						(isSlaveNaked()
								?"[npc.She] immediately moves to obey your order, and obediently steps back and asks,"
								:"[npc.She] immediately moves to obey your order, and obediently drops [npc.her] clothing to the floor."
									+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
						+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
					+ "</p>"
					+ "<p>"
						+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
						+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
						+ " Finally, you command your doll to show you the port in the back of [npc.her] neck, and check it to make sure that it's in perfect condition."
					+ "</p>"
					+ "<p>"
						+ "Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and without a word of complaint, [npc.she] once more does exactly as you say."
					+ "</p>");
				
			} else {
				if(getSlave().getFetishDesire(Fetish.FETISH_MASOCHIST).isPositive() || getSlave().getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()) {
					switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
						case DISLIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out an angry scowl as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
												+ (isSlaveNaked()
														?" Shooting you a furious glare, [npc.she] snarls,"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] snarls,")
												+ " [npc.speech(Fucking "+(Main.game.getPlayer().isFeminine()?"bitch":"bastard")+"! Go on! Take a good look at your <i>property</i>, you sick fuck!)]"
											+ "</p>"
											+ "<p>"
												+ "Despite [npc.her] angry words, you detect an undercurrent of arousal in [npc.namePos] voice,"
													+ " and [npc.she] puts up surprisingly little resistance as you command [npc.herHim] to parade around in front of you."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
													+", and even though this draws yet another angry remark from between your disobedient slave's [npc.lips],"
													+ " you can tell that [npc.she] secretly enjoyed presenting [npc.herself] to you."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a frustrated sigh as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, begrudgingly moves to obey."
											+ (isSlaveNaked()
													?" Glaring at you, [npc.she] snaps,"
													:" As [npc.she] takes [npc.her] clothes off, [npc.she] snaps,")
											+ " [npc.speech(Very well, <i>[npc.pcName]</i>.)]"
										+ "</p>"
										+ "<p>"
											+ "Despite [npc.her] slightly-rebellious tone, you detect an undercurrent of arousal in [npc.namePos] voice,"
												+ " and [npc.she] puts up surprisingly little resistance as you command [npc.herHim] to parade around in front of you."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
												+", and from [npc.her] disappointed sigh, you can tell that [npc.she] enjoyed presenting [npc.herself] to you."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													? "[npc.She] immediately moves to obey your order, but you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													: "[npc.She] immediately moves to obey your order, but you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
												+", and, without a word of complaint, [npc.she] once more does exactly as you say."
											+ " From the way [npc.her] cheeks have flushed, you can tell that [npc.she] enjoyed presenting [npc.herself] to you."
										+ "</p>");
									break;
							}
							break;
						case NEUTRAL:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out a happy cry as [npc.she] hears your order, and quickly moves to obey."
												+ (isSlaveNaked()
														?" Smiling seductively at you, [npc.she] [npc.moansVerb],"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] [npc.moansVerb],")
												+ " [npc.speech(~Mmm~ I hope you enjoy this as much as I do, [npc.pcName]...)]"
											+ "</p>"
											+ "<p>"
												+ "Ignoring [npc.her] words, you command [npc.name] to parade around in front of you; an order which [npc.she] again eagerly carries out."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
												+", which draws a disappointed sigh from between your disobedient slave's [npc.lips]."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a happy cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, quickly moves to obey."
											+ (isSlaveNaked()
													?" Smiling seductively at you, [npc.she] [npc.moansVerb],"
													:" As [npc.she] takes [npc.her] clothes off, [npc.she] [npc.moansVerb],")
											+ " [npc.speech(I hope this is to your satisfaction, [npc.pcName].)]"
										+ "</p>"
										+ "<p>"
											+ "After [npc.sheHas] stripped naked, you command [npc.name] to parade around in front of you; an order which [npc.she] again eagerly carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, with a disappointed sigh, [npc.she] does as you command."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													?"[npc.She] immediately moves to obey your order, and you see the distinct look of arousal in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													:"[npc.She] immediately moves to obey your order, and you see the distinct look of arousal in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, without a word of complaint, [npc.she] once more does exactly as you say."
											+ " From the way [npc.her] cheeks have flushed, you can tell that [npc.she] enjoyed presenting [npc.herself] to you."
										+ "</p>");
									break;
							}
							break;
						case LIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out a happy little cry as [npc.she] hears your order, and quickly moves to obey."
												+ (isSlaveNaked()
														?" Smiling seductively at you, [npc.she] [npc.moansVerb],"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] [npc.moansVerb],")
												+ " [npc.speech(~Mmm~ It's so degrading being forced to do this... I love it...)]"
											+ "</p>"
											+ "<p>"
												+ "Ignoring [npc.her] words, you command [npc.name] to parade around in front of you; an order which [npc.she] again happily carries out."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
													+", which draws a disappointed sigh from between your disobedient slave's [npc.lips]."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a happy little cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, quickly moves to obey."
											+ (isSlaveNaked()
													?" Smiling seductively at you, [npc.she] [npc.moansVerb],"
													:" As [npc.she] takes [npc.her] clothes off, [npc.she] [npc.moansVerb],")
											+ " [npc.speech(I hope this pleases you, [npc.pcName]...)]"
										+ "</p>"
										+ "<p>"
											+ "Ignoring the amorous tone of [npc.her] voice, you command [npc.name] to parade around in front of you; an order which [npc.she] again happily carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, with a disappointed sigh, [npc.she] does as you command."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													?"[npc.She] immediately moves to obey your order, and you see the distinct look of arousal in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													:"[npc.She] immediately moves to obey your order, and you see the distinct look of arousal in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, without a word of complaint, [npc.she] once more does exactly as you say."
											+ " From the way [npc.her] cheeks have flushed, you can tell that [npc.she] enjoyed presenting [npc.herself] to you."
										+ "</p>");
									break;
							}
							break;
					}
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>[npc.Name] loves displaying [npc.herself] like this, and both [npc.her] affection towards you and [npc.her] obedience increases!</i>"
							+ "</p>");
					
				} else {
					switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
						case DISLIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out an angry scowl as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
												+ (isSlaveNaked()
														?" Shooting you a furious glare, [npc.she] snarls,"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] snarls,")
												+ " [npc.speech(Fucking "+(Main.game.getPlayer().isFeminine()?"bitch":"bastard")+"! Go on! Take a good look at your <i>property</i>, you sick fuck!)]"
											+ "</p>"
											+ "<p>"
												+ "Ignoring [npc.her] rebellious words, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
													+", drawing yet another angry remark from between your disobedient slave's [npc.lips]."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a frustrated sigh as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, begrudgingly moves to obey."
												+ (isSlaveNaked()
														?" Glaring at you, [npc.she] snaps,"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] snaps,")
											+ " [npc.speech(Very well, <i>[npc.pcName]</i>.)]"
										+ "</p>"
										+ "<p>"
											+ "Ignoring [npc.her] slightly-rebellious tone, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, with a relieved sigh, [npc.she] does as you command."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													? "[npc.She] immediately moves to obey your order, but you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													: "[npc.She] immediately moves to obey your order, but you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, without a word of complaint, [npc.she] once more does exactly as you say."
										+ "</p>");
									break;
							}
							break;
						case NEUTRAL:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out a flustered cry as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
												+ (isSlaveNaked()
														?" Putting on [npc.her] most pleading expression, [npc.she] whines,"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] whines,")
												+ " [npc.speech(Do I really have to? It's kind of degrading being forced to do this...)]"
											+ "</p>"
											+ "<p>"
												+ "Ignoring [npc.her] words, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
													+", which draws a relieved sigh from between your disobedient slave's [npc.lips]."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a flustered cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, begrudgingly moves to obey."
											+ (isSlaveNaked()
													?" Putting on [npc.her] most pleading expression, [npc.she] whines,"
													:" As [npc.she] takes [npc.her] clothes off, [npc.she] whines,")
											+ " [npc.speech(Just give me a moment, [npc.pcName].)]"
										+ "</p>"
										+ "<p>"
											+ "After [npc.sheIs] stripped naked, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, with a relieved sigh, [npc.she] does as you command."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													?"[npc.She] immediately moves to obey your order, and you see the distinct look of distress in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													:"[npc.She] immediately moves to obey your order, and you see the distinct look of distress in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, without a word of complaint, [npc.she] once more does exactly as you say."
										+ "</p>");
									break;
							}
							break;
						case LIKE:
							switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
								case DISOBEDIENT:
									UtilText.nodeContentSB.append(
												"[npc.She] lets out a sad little cry as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
												+ (isSlaveNaked()
														?" Putting on [npc.her] most pleading expression, [npc.she] whines,"
														:" As [npc.she] takes [npc.her] clothes off, [npc.she] whines,")
												+ " [npc.speech(You know, it's kind of degrading being forced to do this... I thought you liked me...)]"
											+ "</p>"
											+ "<p>"
												+ "Ignoring [npc.her] words, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
												+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
												+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")
													+", which draws a relieved sigh from between your disobedient slave's [npc.lips]."
											+ "</p>");
									break;
								case NEUTRAL:
									UtilText.nodeContentSB.append(
											"[npc.She] lets out a sad little cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, slowly moves to obey."
											+ (isSlaveNaked()
													?" Putting on [npc.her] most pleading expression, [npc.she] whines,"
													:" As [npc.she] takes [npc.her] clothes off, [npc.she] whines,")
											+ " [npc.speech(I thought you liked me, [npc.pcName]... But if this is what you want...)]"
										+ "</p>"
										+ "<p>"
											+ "Ignoring [npc.her] slightly-rebellious protest, you command [npc.name] to parade around in front of you; an order which [npc.she] again reluctantly carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+" Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, with a relieved sigh, [npc.she] does as you command."
										+ "</p>");
									break;
								case OBEDIENT:
									UtilText.nodeContentSB.append(
											(isSlaveNaked()
													?"[npc.She] immediately moves to obey your order, and you see the distinct look of sadness in [npc.her] [npc.eyes] as [npc.she] obediently steps back and asks,"
													:"[npc.She] immediately moves to obey your order, and you see the distinct look of sadness in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
														+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,")
											+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
										+ "</p>"
										+ "<p>"
											+ "You answer in the affirmative, before commanding [npc.name] to parade around in front of you; an order which [npc.she] again dutifully carries out."
											+ " After that, you get [npc.herHim] to "+legsSpreading+partInspection()
											+ " Satisfied with [npc.her] appearance, you tell [npc.name] "+(isSlaveNaked()?"to step back up before you":"to get dressed again")+", and, without a word of complaint, [npc.she] once more does exactly as you say."
										+ "</p>");
									break;
							}
							break;
					}
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>It makes no difference that [npc.name]"
									+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
									+ " you, as being forced to strip and present [npc.herself] to you like a piece of meat has a hugely negative impact on [npc.her] affection towards you, while simultaneously increasing [npc.her] obedience!</i>"
							+ "</p>");
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	private static String partInspection() {
		if(getSlave().hasPenis()) {
			if(getSlave().hasVagina()) {
				return ("[npc.penis+] and [npc.vagina+] for your inspection.");
			} else {
				return ("[npc.penis+] for your inspection.");
			}
			
		} else if(getSlave().hasVagina()) {
			return ("[npc.vagina+] for your inspection.");
			
		} else {
			return ("genderless mound for your inspection.");
		}
	}
	
	
	public static final DialogueNode SLAVE_SPANKING = new DialogueNode("", "", true) { //TODO
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Spanking [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			if(isDoll()) {
				UtilText.nodeContentSB.append("<p>"
						+ "You feel like [npc.name] should be reminded of [npc.her] place, and so after taking a seat you order [npc.herHim] to strip and bend over in your lap."
					+ "</p>"
					+ "<p>"
						+ "[npc.She] immediately moves to obey your order and obediently drops [npc.her] clothing to the floor."
						+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,"
						+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
					+ "</p>"
					+ "<p>"
						+ "You answer in the affirmative, before once again commanding [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as quickly as [npc.she] can."
						+ " As [npc.she] lies down in your lap, presenting you with [npc.her] [npc.ass+], you swiftly bring your [pc.hand] down to smack [npc.her] exposed cheeks."
						+ " A robotic-sounding squeal accompanies the sharp sound of silicone being slapped, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
					+ "</p>"
					+ "<p>"
						+ "You continue this for a little while; your doll obediently holding still in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
						+ " Although [npc.she] squeals and pants in response, you can't help but feel that [npc.sheIs] putting it on for your benefit, and that the punishment isn't hurting [npc.herHim] at all."
						+ " As such, you bring the spanking to and end..."
					+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append("<p>"
								+ "You feel like [npc.name] could do with a reminder of who's in charge, so, taking a seat, you order [npc.herHim] to strip and bend over in your lap."
							+ "</p>"
							+ "<p>");
	
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
											"An angry growl escapes from [npc.her] mouth as [npc.she] hears what you're ordering [npc.herHim] to do, but,"
													+ " understanding that [npc.she] doesn't have any choice in the matter, reluctantly moves to obey."
											+ " As [npc.she] slowly takes [npc.her] clothes off, [npc.she] snaps,"
											+ " [npc.speech(You fucking "+(Main.game.getPlayer().isFeminine()?"bitch":"bastard")+"! I hate you so fucking much!)]"
										+ "</p>"
										+ "<p>"
											+ "Ignoring [npc.her] insolent remarks, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
											+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
											+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
										+ "</p>"
										+ "<p>"
											+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
											+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
										+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										"[npc.She] lets out a flustered cry as [npc.she] hears what you're ordering [npc.herHim] to do, but,"
												+ " trying to keep [npc.her] emotions under control, reluctantly moves to obey."
										+ " As [npc.she] slowly takes [npc.her] clothes off, [npc.she] sighs,"
										+ " [npc.speech(This will just take a moment, <i>[npc.pcName]</i>.)]"
									+ "</p>"
									+ "<p>"
										+ "Ignoring [npc.her] slightly-rebellious tone, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										"[npc.She] immediately moves to obey your order, but you see the distinct look of hatred in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
										+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,"
										+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
									+ "</p>"
									+ "<p>"
										+ "You answer in the affirmative, before once again commanding [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
						}
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
										"[npc.She] lets out a flustered cry as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
										+ " As [npc.she] takes [npc.her] clothes off, [npc.she] whines,"
										+ " [npc.speech(Do I really have to? It's kind of degrading being forced to do this...)]"
									+ "</p>"
									+ "<p>"
										+ "Ignoring [npc.her] words, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										"[npc.She] lets out a flustered cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, begrudgingly moves to obey."
										+ " As [npc.she] takes [npc.her] clothes off, [npc.she] whines,"
										+ " [npc.speech(Just give me a moment, [npc.pcName].)]"
									+ "</p>"
									+ "<p>"
										+ "After [npc.sheIs] stripped naked, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										"[npc.She] immediately moves to obey your order, but you see the distinct look of distress in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
										+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,"
										+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
									+ "</p>"
									+ "<p>"
										+ "You answer in the affirmative, before once again commanding [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] carries out as quickly as [npc.she] can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
						}
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								UtilText.nodeContentSB.append(
										"[npc.She] lets out a sad little cry as [npc.she] hears your order, but, realising that [npc.she] really doesn't have any choice in the matter, begrudgingly moves to obey."
										+ " As [npc.she] takes [npc.her] clothes off, [npc.she] whines,"
										+ " [npc.speech(You know, it's kind of degrading being forced to do this... I thought you liked me...)]"
									+ "</p>"
									+ "<p>"
										+ "Ignoring [npc.her] words, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
							case NEUTRAL:
								UtilText.nodeContentSB.append(
										"[npc.She] lets out a sad little cry as [npc.she] hears your order, but, trying to keep [npc.her] emotions under control, slowly moves to obey."
										+ " As [npc.she] takes [npc.her] clothes off, [npc.she] whines,"
										+ " [npc.speech(I thought you liked me, [npc.pcName]... But if this is what you want...)]"
									+ "</p>"
									+ "<p>"
										+ "Ignoring [npc.her] words, you once again command [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
							case OBEDIENT:
								UtilText.nodeContentSB.append(
										"[npc.She] immediately moves to obey your order, but you see the distinct look of sadness in [npc.her] [npc.eyes] as [npc.she] obediently drops [npc.her] clothing to the floor."
										+ " As [npc.she] takes the last of [npc.her] clothes off, [npc.she] asks,"
										+ " [npc.speech(Is this to your pleasure, [npc.pcName]?)]"
									+ "</p>"
									+ "<p>"
										+ "You answer in the affirmative, before once again commanding [npc.name] to bend [npc.herself] over your knee; an order which [npc.she] again carries out as slowly as [npc.she] possibly can."
										+ " As [npc.she] lies down in your lap, presenting [npc.her] [npc.ass+], you let out a victorious laugh, before swiftly bringing your [pc.hand] down to smack [npc.her] exposed cheeks."
										+ " A squeal accompanies the sharp sound of flesh being whacked, which drives you to deliver yet another blow to [npc.her] [npc.ass]."
									+ "</p>"
									+ "<p>"
										+ "You continue this for a little while; your slave squirming and squealing in your lap as you rain down blow after blow upon [npc.her] vulnerable backside."
										+ " Eventually, however, you feel as though [npc.sheIs] been taught [npc.her] lesson, and you order [npc.herHim] to stand up and put [npc.her] clothes on once more."
									+ "</p>");
								break;
						}
						break;
				}
	
				if(getSlave().getFetishDesire(Fetish.FETISH_MASOCHIST).isPositive()) {
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>As [npc.name] is a masochist, spanking [npc.herHim] has actually made [npc.herHim] like you more, as well as increasing [npc.her] obedience!</i>"
							+ "</p>");
					
				} else {
					UtilText.nodeContentSB.append(
							"<p>"
								+ "<i>It makes no difference that [npc.name]"
									+ " <span style='"+getSlave().getAffectionLevel(Main.game.getPlayer()).getColour().toWebHexString()+"'>"+getSlave().getAffectionLevel(Main.game.getPlayer()).getDescriptor()+"</span>"
									+ " you, as being forced to present [npc.herself] for a disciplinary spanking has a hugely negative impact on [npc.her] affection towards you, while simultaneously increasing [npc.her] obedience!</i>"
							+ "</p>");
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode SLAVE_MOLEST = new DialogueNode("", "", true) {
		@Override
		public DialogueNodeType getDialogueNodeType() {
			return DialogueNodeType.OCCUPANT_MANAGEMENT;
		}
		
		@Override
		public String getLabel(){
			return UtilText.parse(getSlave(), "Molesting [npc.Name]");
		}

		@Override
		public String getContent() {
			UtilText.nodeContentSB.setLength(0);

			if(isDoll()) {
				UtilText.nodeContentSB.append("<p>"
							+ "Not being able to resist the allure of [npc.namePos] [npc.feminine] body, you walk around behind [npc.her] back, before stepping forwards and wrapping your [pc.arms] around [npc.herHim]."
							+ " Your doll obediently holds still as you grab [npc.her] [npc.breasts+], and lets out [npc.a_moan+] as you move your touch down between [npc.her] [npc.legs]."
						+ "</p>"
						+ "<p>"
							+ "[npc.speech(Yes, [pc.name], please use me however you want,)] [npc.she] pants."
						+ "</p>"
						+ "<p>"
							+ "Doing just that, you continue groping and molesting [npc.name], only stopping once you've had your fun and explored every inch of [npc.her] silicone body."
							+ " [pc.Stepping] back, you wonder what to do next..."
						+ "</p>");
				
			} else {
				UtilText.nodeContentSB.append("<p>"
							+ "Not being able to resist the allure of [npc.namePos] [npc.feminine] body, you walk around behind [npc.her] back, before stepping forwards and wrapping your [pc.arms] around [npc.herHim].");
	
				String firstCry = "a horrified cry";
				String firstReaction = "instantly starts squirming and trying to pull away from your touch";
				String firstSpeech = "Fuck off! Leave me alone!";
				String firstPCReaction = "[npc.Her] words do nothing to deter your advance upon [npc.her] unwilling body";
				
				String secondReaction = "[npc.She] continues to shout and struggle";
				String thirdReaction = ", despite [npc.her] protests,";
				String secondSpeech = "[npc.speech(Stop it! F-Fuck off already!)]"
							+ " [npc.she] shouts, [npc.her] words falling on deaf ears as you continue having your fun.";
				
				String finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to dash across to the other side of the room, you look [npc.herHim] up and down, grinning."
							+ " [npc.She] continues to spit curses and tell you to leave [npc.herHim] alone, and you wonder if you should do as your slave asks, or do something else with [npc.herHim]...";
				
				switch(AffectionLevelBasic.getAffectionLevelFromValue(getSlave().getAffection(Main.game.getPlayer()))) {
					case DISLIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								break;
							case NEUTRAL:
								firstCry = "an uncomfortable cry";
								firstReaction = "starts squirming and trying to shift [npc.her] body away from your touch";
								firstSpeech = "C-Can you stop this now?!";
								firstPCReaction = "[npc.Her] words do nothing to deter your advance upon [npc.her] unwilling body";
								
								secondReaction = "[npc.She] continues to struggle in discomfort";
								thirdReaction = ", despite [npc.her] reluctance,";
								secondSpeech = "[npc.speech(P-Please! S-Stop it now!)]"
											+ " [npc.she] cries, [npc.her] words falling on deaf ears as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to dash across to the other side of the room, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] avoids your gaze as [npc.she] asks you to leave [npc.herHim] alone, and you wonder if you should do as your slave requests, or do something else with [npc.herHim]...";
								break;
							case OBEDIENT:
								firstCry = "a shocked cry";
								firstReaction = "obediently holds [npc.her] body still for you";
								firstSpeech = "I am yours to use, [npc.pcName].";
								firstPCReaction = "[npc.Her] cold words do nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to obediently allow [npc.herself] to be used";
								thirdReaction = ", despite [npc.her] cold attitude,";
								secondSpeech = "[npc.speech(Is this to your satisfaction, [npc.pcName]?)]"
											+ " [npc.she] dutifully asks, showing no sign of emotion as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a step back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] obediently asks you if there's anything else that's required of [npc.herHim], which makes you wonder if you should do something else with [npc.herHim]...";
								break;
						}
						break;
					case NEUTRAL:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								firstCry = "a startled cry";
								firstReaction = "starts squirming and trying to shift [npc.her] body away from your touch";
								firstSpeech = "D-Do you have to do this?";
								firstPCReaction = "[npc.Her] worried tone does nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to struggle a little";
								thirdReaction = ", despite [npc.her] less-than-enthusiastic attitude,";
								secondSpeech = "[npc.speech(How much longer is this going to do on for, [npc.pcName]?)]"
											+ " [npc.she] asks, [npc.her] disobedient questioning receiving no answer as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a few steps back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] lets out a sigh of relief as [npc.she] asks you to leave [npc.herHim] alone now, and you wonder if you should do as your slave requests, or do something else with [npc.herHim]...";
								break;
							case NEUTRAL:
								firstCry = "a half-suppressed cry";
								firstReaction = "starts squirming and trying to shift [npc.her] body away from your touch";
								firstSpeech = "[npc.pcName], do you really have to do this?";
								firstPCReaction = "[npc.Her] worried tone does nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to struggle a little";
								thirdReaction = ", despite [npc.her] less-than-enthusiastic attitude,";
								secondSpeech = "[npc.speech(Are you almost done, [npc.pcName]?)]"
											+ " [npc.she] asks, [npc.her] disobedient questioning receiving no answer as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a few steps back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] lets out a little sigh of relief as [npc.she] avoids your gaze, and you wonder if you should do something else with [npc.herHim]...";
								break;
							case OBEDIENT:
								firstCry = "a shocked cry";
								firstReaction = "obediently holds [npc.her] body still for you";
								firstSpeech = "I am yours to use, [npc.pcName].";
								firstPCReaction = "[npc.Her] cold words do nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to obediently allow [npc.herself] to be used";
								thirdReaction = ", despite [npc.her] cold attitude,";
								secondSpeech = "[npc.speech(Is this to your satisfaction, [npc.pcName]?)]"
											+ " [npc.she] dutifully asks, showing no sign of emotion as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a step back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] obediently asks you if there's anything else that's required of [npc.herHim], which makes you wonder if you should do something else with [npc.herHim]...";
								break;
						}
						break;
					case LIKE:
						switch(ObedienceLevelBasic.getObedienceLevelFromValue(getSlave().getObedienceValue())) {
							case DISOBEDIENT:
								firstCry = "a startled cry";
								firstReaction = "starts squirming and trying to shift [npc.her] body away from your touch";
								firstSpeech = "[npc.PcName]! I thought you liked me?! W-Why are you doing this?!";
								firstPCReaction = "[npc.Her] distressed tone does nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to struggle a little";
								thirdReaction = ", despite [npc.her] less-than-enthusiastic attitude,";
								secondSpeech = "[npc.speech([npc.PcName], please! I-I just wanted a hug...)]"
											+ " [npc.she] sighs, [npc.her] disobedient remark receiving no answer as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a few steps back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] lets out a sigh of relief as [npc.she] smiles up at you, and you wonder if you should do something else with [npc.herHim] now...";
								break;
							case NEUTRAL:
								firstCry = "a little cry";
								firstReaction = "holds [npc.her] body still for you";
								firstSpeech = "[npc.PcName], do you really have to do this?";
								firstPCReaction = "[npc.Her] worried tone does nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to hold still";
								thirdReaction = ", despite [npc.her] slightly-disobedient attitude,";
								secondSpeech = "[npc.speech(Is this to your liking, [npc.pcName]?)]"
											+ " [npc.she] asks, [npc.her] questioning receiving no answer as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a few steps back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] lets out a little sigh of relief as [npc.she] avoids your gaze, and you wonder if you should do something else with [npc.herHim]...";
								break;
							case OBEDIENT:
								firstCry = "a little gasp";
								firstReaction = "obediently holds [npc.her] body still for you";
								firstSpeech = "I am yours to use, [npc.pcName].";
								firstPCReaction = "[npc.Her] cold words do nothing to deter your advance upon [npc.her] body";
								
								secondReaction = "[npc.She] continues to obediently allow [npc.herself] to be used";
								thirdReaction = ", despite [npc.her] cold attitude,";
								secondSpeech = "[npc.speech(Is this to your satisfaction, [npc.pcName]?)]"
											+ " [npc.she] dutifully asks, showing no sign of emotion as you continue having your fun.";
								
								finalDescription = "Eventually, you feel as though you've had enough, and, releasing [npc.name] to allow [npc.herHim] to take a step back, you look [npc.herHim] up and down, grinning."
											+ " [npc.She] obediently asks you if there's anything else that's required of [npc.herHim], which makes you wonder if you should do something else with [npc.herHim]...";
								break;
						}
						break;
				}
				
				
				
				if(getSlave().hasBreasts()) {
					UtilText.nodeContentSB.append(
								" Your [pc.hands] reach up to cup and squeeze [npc.her] [npc.breasts+], drawing "+firstCry+" from [npc.namePos] mouth as [npc.she] "+firstReaction+","
								+ " [npc.speech("+firstSpeech+")]"
							+ "</p>"
							+ "<p>"
								+ firstPCReaction+", and, with one [pc.hand] still squeezing [npc.her] [npc.breasts+], you slip the other down #IF(npc.hasLegs())between [npc.her] [npc.legs]#ELSEto [npc.her] groin#ENDIF.");
				} else {
					UtilText.nodeContentSB.append(
							" Your [pc.hands] reach up to run over [npc.her] chest, drawing "+firstCry+" from [npc.namePos] mouth as [npc.she] "+firstReaction+","
							+ " [npc.speech("+firstSpeech+")]"
						+ "</p>"
						+ "<p>"
							+ firstPCReaction+", and, with one [pc.hand] still pressed against [npc.her] chest, you slip the other down #IF(npc.hasLegs())between [npc.her] [npc.legs]#ELSEto [npc.her] groin#ENDIF.");
				}
	
				if(getSlave().hasVagina() && getSlave().hasPenis()) {
					UtilText.nodeContentSB.append(
								" "+secondReaction+" as you grope, stroke, and fondle [npc.her] [npc.penis+], and you can't help but [pc.moan] into your slave's [npc.ear] as"+thirdReaction+" you feel [npc.her] [npc.cock+] growing hard under your touch."
								+ " Dropping your [pc.hand] down yet further, you shift your attention to [npc.her] [npc.pussy+],"
									+ " grinning once again as you feel that your stimulation of [npc.her] [npc.clit+] and [npc.labia+] have already gotten [npc.her] wet."
							+ "</p>");
					
				} else if(getSlave().hasVagina()) {
					UtilText.nodeContentSB.append(
							" "+secondReaction+" as you grope, stroke, and probe at [npc.her] [npc.clit+] and [npc.labia+], and you can't help but [pc.moan] into your slave's [npc.ear] as"+thirdReaction+" you feel [npc.her] [npc.pussy+]"
									+ " is already wet from your touch."
						+ "</p>");
					
				} else if(getSlave().hasPenis()) {
					UtilText.nodeContentSB.append(
							" "+secondReaction+" as you grope, stroke, and fondle [npc.her] [npc.penis+], and you can't help but [pc.moan] into your slave's [npc.ear] as"+thirdReaction+" you feel [npc.her] [npc.cock+] growing hard under your touch."
						+ "</p>");
					
				} else {
					UtilText.nodeContentSB.append(
							" "+secondReaction+" as you grope, stroke, and probe at [npc.her] genderless mound, and you can't help but [pc.moan] into your slave's [npc.ear] as"+thirdReaction+" you feel [npc.herHim] thrust [npc.her] [npc.hips] out against your touch."
						+ "</p>");
					
				}
				UtilText.nodeContentSB.append(
						"<p>"
							+ secondSpeech
						+ "</p>"
						+ "<p>"
							+ finalDescription
						+ "</p>");
				
				
				UtilText.nodeContentSB.append(
						"<p>"
							+ "<i>Molesting [npc.name] has helped reinforce in [npc.her] mind that [npc.she] belongs to you, increasing [npc.her] obedience!");
				if(getSlave().isAttractedTo(Main.game.getPlayer())) {
					if(getSlave().hasFetish(Fetish.FETISH_SUBMISSIVE) || getSlave().hasFetish(Fetish.FETISH_NON_CON_SUB)) {
						UtilText.nodeContentSB.append(
									" As [npc.sheIs] both attracted to you, as well as"
									+ (getSlave().hasFetish(Fetish.FETISH_SUBMISSIVE)
											?(getSlave().hasFetish(Fetish.FETISH_NON_CON_SUB)
													?" being both a submissive and turned on by non-consensual encounters,"
													:" being a submissive,")
											:" being turned on by non-consensual encounters,")
									+ " your dominant, non-consensual use of [npc.namePos] body has resulted in [npc.herHim] liking you more!</i>"
								+ "</p>");
					} else {
						UtilText.nodeContentSB.append(
								" As [npc.sheIs] attracted to you, your dominant, non-consensual use of [npc.namePos] body has resulted in [npc.herHim] neither losing nor gaining affection towards you!</i>"
							+ "</p>");
					}
				} else {
					if(getSlave().hasFetish(Fetish.FETISH_SUBMISSIVE) || getSlave().hasFetish(Fetish.FETISH_NON_CON_SUB)) {
						UtilText.nodeContentSB.append(
								" Despite the fact that [npc.sheIs] not attracted to you,"
								+ (getSlave().hasFetish(Fetish.FETISH_SUBMISSIVE)
										?(getSlave().hasFetish(Fetish.FETISH_NON_CON_SUB)
												?" by being both a submissive and turned on by non-consensual encounters,"
												:" by being a submissive,")
										:" by being turned on by non-consensual encounters,")
								+ " your dominant, non-consensual use of [npc.namePos] body has resulted in [npc.herHim] neither losing nor gaining affection towards you!</i>"
							+ "</p>");
					} else {
						UtilText.nodeContentSB.append(
								" As [npc.sheIs] not attracted to you, your dominant, non-consensual use of [npc.namePos] body has resulted in [npc.herHim] losing a lot of affection towards you!</i>"
							+ "</p>");
					}
				}
			}
			
			UtilText.nodeContentSB.append(getFooterText());
			
			return UtilText.parse(getSlave(), UtilText.nodeContentSB.toString());
		}

		@Override
		public String getResponseTabTitle(int index) {
			return SLAVE_START.getResponseTabTitle(index);
		}

		@Override
		public Response getResponse(int responseTab, int index) {
			return SLAVE_START.getResponse(responseTab, index);
		}
	};
	
	public static final DialogueNode AFTER_SEX = new DialogueNode("Step back", "", true) {
		@Override
		public String getDescription(){
			return "Now that you've had your fun, you can step back and leave [npc.name] to recover.";
		}
		@Override
		public String getContent() {
			if(Main.sex.getAllParticipants(false).size()==4) {
				List<GameCharacter> sexParticipants = new ArrayList<GameCharacter>(charactersPresent);
				return UtilText.parseFromXMLFile(getTextFilePath(), "AFTER_SEX_GANGBANG_3", sexParticipants);
				
			} else if(Main.sex.getAllParticipants(false).size()==5) {
				List<GameCharacter> sexParticipants = new ArrayList<GameCharacter>(charactersPresent);
				return UtilText.parseFromXMLFile(getTextFilePath(), "AFTER_SEX_GANGBANG_4", sexParticipants);
				
			} else {
				if(Main.sex.getNumberOfOrgasms(getSlave()) >= getSlave().getOrgasmsBeforeSatisfied()) {
					return UtilText.parseFromXMLFile(getTextFilePath(), "AFTER_SEX", getSlave());
				} else {
					return UtilText.parseFromXMLFile(getTextFilePath(), "AFTER_SEX_NO_ORGASM", getSlave());
				}
			}
		}
		@Override
		public Response getResponse(int responseTab, int index) {
			if(index == 1) {
				return new Response("Continue", "Decide what to do next.", SLAVE_START_NO_CONTENT) {
					@Override
					public void effects() {
						SlaveDialogue.initDialogue(getSlave(), false); // Need to re-init this dialogue as it gets cleared in Game turnUpdate during this scene.
						Main.game.appendToTextEndStringBuilder(UtilText.parseFromXMLFile(getTextFilePath(), "AFTER_SEX_CONTINUE", getSlave()));
						if(!getSlave().isAsleep() && !isDollStatue()) {
							Main.game.appendToTextEndStringBuilder(getFooterText());
						}
//						Main.game.appendToTextEndStringBuilder(getSlaveStartCoreContent());
					}
				};
			}
			return null;
		}
	};
	
}
