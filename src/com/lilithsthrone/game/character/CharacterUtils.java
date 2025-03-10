package com.lilithsthrone.game.character;

import java.io.File;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.lilithsthrone.game.character.body.valueEnums.*;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringCategory;
import com.lilithsthrone.game.character.race.Disposition;
import com.lilithsthrone.game.character.race.AbstractSubspecies;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.FurryPreference;
import com.lilithsthrone.game.character.race.RacialBody;
import com.lilithsthrone.game.character.race.AbstractRacialBody;
import com.lilithsthrone.game.character.race.Race;
import com.lilithsthrone.game.character.effects.Perk;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.lilithsthrone.controller.xmlParsing.XMLLoadException;
import com.lilithsthrone.game.PropertyValue;
import com.lilithsthrone.game.character.attributes.Attribute;
import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.body.Antenna;
import com.lilithsthrone.game.character.body.Arm;
import com.lilithsthrone.game.character.body.Ass;
import com.lilithsthrone.game.character.body.Body;
import com.lilithsthrone.game.character.body.BodyPartInterface;
import com.lilithsthrone.game.character.body.Breast;
import com.lilithsthrone.game.character.body.BreastCrotch;
import com.lilithsthrone.game.character.body.CoverableArea;
import com.lilithsthrone.game.character.body.Ear;
import com.lilithsthrone.game.character.body.Eye;
import com.lilithsthrone.game.character.body.Face;
import com.lilithsthrone.game.character.body.Hair;
import com.lilithsthrone.game.character.body.Horn;
import com.lilithsthrone.game.character.body.Leg;
import com.lilithsthrone.game.character.body.Penis;
import com.lilithsthrone.game.character.body.Tail;
import com.lilithsthrone.game.character.body.Tentacle;
import com.lilithsthrone.game.character.body.Torso;
import com.lilithsthrone.game.character.body.Vagina;
import com.lilithsthrone.game.character.body.Wing;
import com.lilithsthrone.game.character.body.abstractTypes.AbstractFaceType;
import com.lilithsthrone.game.character.body.abstractTypes.AbstractHairType;
import com.lilithsthrone.game.character.body.abstractTypes.AbstractTailType;
import com.lilithsthrone.game.character.body.coverings.AbstractBodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.Covering;
import com.lilithsthrone.game.character.body.tags.BodyPartTag;
import com.lilithsthrone.game.character.body.types.AntennaType;
import com.lilithsthrone.game.character.body.types.ArmType;
import com.lilithsthrone.game.character.body.types.AssType;
import com.lilithsthrone.game.character.body.types.BreastType;
import com.lilithsthrone.game.character.body.types.EarType;
import com.lilithsthrone.game.character.body.types.EyeType;
import com.lilithsthrone.game.character.body.types.FaceType;
import com.lilithsthrone.game.character.body.types.HairType;
import com.lilithsthrone.game.character.body.types.HornType;
import com.lilithsthrone.game.character.body.types.LegType;
import com.lilithsthrone.game.character.body.types.PenisType;
import com.lilithsthrone.game.character.body.types.TailType;
import com.lilithsthrone.game.character.body.types.TentacleType;
import com.lilithsthrone.game.character.body.types.TorsoType;
import com.lilithsthrone.game.character.body.types.VaginaType;
import com.lilithsthrone.game.character.body.types.WingType;
import com.lilithsthrone.game.character.effects.StatusEffect;
import com.lilithsthrone.game.character.fetishes.AbstractFetish;
import com.lilithsthrone.game.character.fetishes.Fetish;
import com.lilithsthrone.game.character.fetishes.FetishDesire;
import com.lilithsthrone.game.character.fetishes.FetishPreference;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.gender.PronounType;
import com.lilithsthrone.game.character.markings.AbstractTattooType;
import com.lilithsthrone.game.character.markings.Tattoo;
import com.lilithsthrone.game.character.markings.TattooType;
import com.lilithsthrone.game.character.markings.TattooWriting;
import com.lilithsthrone.game.character.markings.TattooWritingStyle;
import com.lilithsthrone.game.character.npc.NPC;
import com.lilithsthrone.game.character.npc.dominion.Cultist;
import com.lilithsthrone.game.character.npc.dominion.DominionSuccubusAttacker;
import com.lilithsthrone.game.character.npc.misc.GenericAndrogynousNPC;
import com.lilithsthrone.game.character.persona.Name;
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.persona.Occupation;
import com.lilithsthrone.game.character.persona.PersonalityTrait;
import com.lilithsthrone.game.character.persona.SexualOrientation;
import com.lilithsthrone.game.inventory.AbstractCoreItem;
import com.lilithsthrone.game.inventory.InventorySlot;
import com.lilithsthrone.game.inventory.ItemTag;
import com.lilithsthrone.game.inventory.Rarity;
import com.lilithsthrone.game.inventory.SetBonus;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.clothing.AbstractClothingType;
import com.lilithsthrone.game.inventory.clothing.BlockedParts;
import com.lilithsthrone.game.inventory.clothing.BodyPartClothingBlock;
import com.lilithsthrone.game.inventory.clothing.ClothingType;
import com.lilithsthrone.game.inventory.item.AbstractItem;
import com.lilithsthrone.game.inventory.item.ItemType;
import com.lilithsthrone.game.inventory.outfit.AbstractOutfit;
import com.lilithsthrone.game.inventory.outfit.OutfitType;
import com.lilithsthrone.game.inventory.weapon.AbstractWeapon;
import com.lilithsthrone.game.sex.SexAreaOrifice;
import com.lilithsthrone.game.sex.SexAreaPenetration;
import com.lilithsthrone.game.sex.SexParticipantType;
import com.lilithsthrone.game.sex.SexType;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.ColourListPresets;
import com.lilithsthrone.utils.colours.PresetColour;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

/**
 * @since 0.1.67
 * @version 0.4.2
 * @author Innoxia, tukaima, Maxis
 */
public class CharacterUtils {

	public void saveCharacterAsXML(PlayerCharacter character){
		try {
//			long timeStart = System.nanoTime();
//			System.out.println(timeStart);

			Document doc = Main.getDocBuilder().newDocument();

			Element properties = doc.createElement("playerCharacter");
			doc.appendChild(properties);

			// Modify birth date so that imported characters are the same age:
			character.saveAsXML(properties, doc);

//			System.out.println("Difference2: "+(System.nanoTime()-timeStart)/1000000000f);

			Transformer transformer1 = Main.transformerFactory.newTransformer();
			transformer1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();

//			System.out.println("Difference3: "+(System.nanoTime()-timeStart)/1000000000f);

			transformer1.transform(new DOMSource(doc), new StreamResult(writer));

//			System.out.println("Difference4: "+(System.nanoTime()-timeStart)/1000000000f);

//			String output = writer.getBuffer().toString();
//			System.out.println("Difference: "+(System.nanoTime()-timeStart)/1000000000f);
//			System.out.println(output);

			// Save this xml:
			Transformer transformer = Main.transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			File dir = new File("data/");
			dir.mkdir();

			File dirCharacter = new File("data/characters/");
			dirCharacter.mkdir();

			int saveNumber = 0;
			String saveLocation = "data/characters/"+character.getName(false)+"_day"+Main.game.getDayNumber()+".xml";
			if(new File("data/characters/"+character.getName(false)+"_day"+Main.game.getDayNumber()+".xml").exists())
				saveLocation = "data/characters/"+character.getName(false)+"_day"+Main.game.getDayNumber()+"("+saveNumber+").xml";

			while(new File("data/characters/"+character.getName(false)+"_day"+Main.game.getDayNumber()+"("+saveNumber+").xml").exists()) {
				saveNumber++;
				saveLocation = "data/characters/"+character.getName(false)+"_day"+Main.game.getDayNumber()+"("+saveNumber+").xml";
			}

			StreamResult result = new StreamResult(saveLocation);

			transformer.transform(source, result);

		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static StringBuilder characterImportLog;
	public String getCharacterImportLog() {
		return characterImportLog.toString();
	}

	public void appendToImportLog(StringBuilder log, String message) {
		if(log!=null) {
			log.append(message);
		}
	}

	public PlayerCharacter startLoadingCharacterFromXML(){
		return new PlayerCharacter(new NameTriplet("Player"), 1, null, Gender.M_P_MALE, Subspecies.HUMAN, RaceStage.HUMAN, WorldType.DOMINION, PlaceType.DOMINION_AUNTS_HOME);
	}

	public PlayerCharacter loadCharacterFromXML(File xmlFile, PlayerCharacter importedCharacter, CharacterImportSetting... settings){

		characterImportLog = new StringBuilder();

		if (xmlFile.exists()) {
			try {
				Document doc = Main.getDocBuilder().parse(xmlFile);

				// Cast magic:
				doc.getDocumentElement().normalize();

				if(doc.getElementsByTagName("playerCharacter").item(0) == null) { // Support for older versions:
					importedCharacter = PlayerCharacter.loadFromXML(characterImportLog, ((Element) doc.getElementsByTagName("character").item(0)), doc, settings);
				} else {
					importedCharacter = PlayerCharacter.loadFromXML(characterImportLog, (Element) ((Element) doc.getElementsByTagName("playerCharacter").item(0)).getElementsByTagName("character").item(0), doc, settings);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return importedCharacter;
	}

	public RaceStage getRaceStageFromPreferences(FurryPreference preference, Gender gender, AbstractSubspecies species) {
		RaceStage raceStage = RaceStage.PARTIAL;

		switch(preference) {
			case HUMAN:
				return RaceStage.HUMAN;
			case MINIMUM:
				return RaceStage.PARTIAL;
			case REDUCED:
				return Util.randomItemFrom(Util.newArrayListOfValues(RaceStage.PARTIAL, RaceStage.LESSER));
			case NORMAL:
				return Util.randomItemFrom(Util.newArrayListOfValues(RaceStage.PARTIAL, RaceStage.LESSER, RaceStage.GREATER));
			case MAXIMUM:
				return RaceStage.GREATER;
		}

		return raceStage;
	}

	public Body generateBody(GameCharacter linkedCharacter, Gender startingGender, GameCharacter mother, GameCharacter father) {
		return generateBody(linkedCharacter, startingGender, mother, father, father==null?null:father.getBody());
	}

	public Body generateBody(GameCharacter linkedCharacter, Gender startingGender, GameCharacter mother, GameCharacter father, Body fatherBody) {
		Body body = null;
		boolean takesAfterMother = true;
		boolean raceFromMother = true;
		Body motherBody = mother.getBody();

		// If the mother is feral, preGeneratedBodies are not taken into account, as the offspring must be feral:
		if(!mother.isFeral() && father!=null) {
			body = AbstractSubspecies.getPreGeneratedBody(linkedCharacter, startingGender, motherBody, fatherBody);
		}

		if(fatherBody==null) {
			fatherBody = motherBody;
		}

		AbstractRacialBody motherGenericBody = RacialBody.valueOfRace(body==null?motherBody.getTrueRace():body.getTrueRace());
		AbstractRacialBody fatherGenericBody = RacialBody.valueOfRace(body==null?fatherBody.getTrueRace():body.getTrueRace());
		AbstractSubspecies raceTakesAfter = mother.getTrueSubspecies();
		AbstractSubspecies raceTakesAfterFlesh = mother.getBody().getFleshSubspecies();

		if(father==null) {
			father = mother;
		}

		if (mother.getRace().isMaterialRace()){ motherGenericBody = mother.getBody().getFleshSubspecies().getRace().getRacialBody(); }
		if (father.getRace().isMaterialRace()){ fatherGenericBody = father.getBody().getFleshSubspecies().getRace().getRacialBody(); }

		boolean motherHuman = motherBody.getTorsoType().getRace()==Race.HUMAN;
		boolean fatherHuman = fatherBody.getTorsoType().getRace()==Race.HUMAN;

		if(body==null) {
			AbstractRacialBody startingBodyType = RacialBody.HUMAN;
			RaceStage stage = RaceStage.HUMAN;

			// Core body type is random:
			if((fatherHuman && !motherHuman) || (Math.random()<=0.5 && (!fatherHuman?!motherHuman:motherHuman))) {
				if(mother.isTaur()) {
					if(mother.getArmRace()!=Race.HUMAN) {
						startingBodyType = motherGenericBody;
						stage = mother.getRaceStage();
					}
				} else {
					startingBodyType = motherGenericBody;
					stage = mother.getRaceStage();
				}

			} else {
				if(fatherBody.isTaur()) {
					if(fatherBody.getArmType().getRace()!=Race.HUMAN) {
						startingBodyType = fatherGenericBody;
						stage = fatherBody.getRaceStage();
					}
				} else {
					startingBodyType = fatherGenericBody;
					stage = fatherBody.getRaceStage();
				}
				raceTakesAfter = fatherBody.getTrueSubspecies();
				raceTakesAfterFlesh = fatherBody.getFleshSubspecies();
				takesAfterMother = false;
				raceFromMother = false;
			}

			if(!takesAfterMother && fatherBody.isFeral()) { // Offspring from a feral father are always fully furry
				stage = RaceStage.GREATER;

			} else {
				// If one partner is a human, race stage has a 66% chance to be lowered.
				if((motherHuman && !fatherHuman) || (!motherHuman && fatherHuman)) {
					if(stage == RaceStage.GREATER) {
						double rnd = Math.random();
						if(rnd<0.33) {
							stage = RaceStage.PARTIAL_FULL;
						} else if(rnd<0.66) {
							stage = RaceStage.LESSER;
						}

					} else if(stage == RaceStage.LESSER) {
						double rnd = Math.random();
						if(rnd<0.33) {
							stage = RaceStage.PARTIAL;
						} else if(rnd<0.66) {
							stage = RaceStage.PARTIAL_FULL;
						}

					} else if(stage == RaceStage.PARTIAL_FULL) {
						double rnd = Math.random();
						if(rnd<0.66) {
							stage = RaceStage.PARTIAL;
						}
					}
				}
			}

			body = generateBody(linkedCharacter, startingGender, startingBodyType, stage);
		}

		linkedCharacter.setGenderIdentity(startingGender);
		body.setBodyMaterial(mother.getBodyMaterial());
		if (Math.random() < 0.20) {
			body.setBodyMaterial(father.getBodyMaterial());
		}

		applyGenetics(linkedCharacter, body, motherBody, fatherBody, raceFromMother);

		// The applyRaceChanges and applySpeciesChanges methods sometimes change covering colours and then call updateCoverings(), which will result in this character's covering colours being unrelated to genetics
		// To fix, coverings are saved and then restored after the two methods have been called
		Map<AbstractBodyCoveringType, Covering> preChangesCoverings = body.getCoverings();
		if (!raceTakesAfter.getRace().isMaterialRace()) {
			raceTakesAfter.getRace().applyRaceChanges(body);
			raceTakesAfter.applySpeciesChanges(body);
		} else {
			//System.err.println(raceTakesAfter.getName(null) + ',' + raceTakesAfterFlesh.getName(null));
			raceTakesAfterFlesh.getRace().applyRaceChanges(body);
			raceTakesAfterFlesh.applySpeciesChanges(body);
		}
		body.setCoverings(preChangesCoverings);


		return body;
	}

	/**
	 * @param offspring The offspring GameCharacter.
	 * @param body The body of the offspring to which genetics should be applied.
	 * @param motherBody The body of the mother.
	 * @param fatherBody The body of the father.
	 * @param raceFromMother Whether or not the offspring has inherited their race from their mother.
	 */
	public void applyGenetics(GameCharacter offspring, Body body, Body motherBody, Body fatherBody, boolean raceFromMother) {
		// Genetics! (Sort of...)

		NPC blankNPC = Main.game.getNpc(GenericAndrogynousNPC.class);
		boolean takesAfterMother = true;
		Gender startingGender = body.getGender();
		boolean feminineGender = startingGender.isFeminine();
		AbstractRacialBody motherGenericBody = RacialBody.valueOfRace(body==null?motherBody.getTrueRace():body.getTrueRace());
		AbstractRacialBody fatherGenericBody = RacialBody.valueOfRace(body==null?fatherBody.getTrueRace():body.getTrueRace());
		if (motherBody.getRace().isMaterialRace()){ motherGenericBody = motherBody.getFleshSubspecies().getRace().getRacialBody(); }
		if (fatherBody.getRace().isMaterialRace()){ fatherGenericBody = fatherBody.getFleshSubspecies().getRace().getRacialBody(); }

		// Takes other features from the parent closest to their femininity:
		if(Math.abs(motherBody.getFemininity()-body.getFemininity()) > Math.abs(fatherBody.getFemininity()-body.getFemininity())) {
			takesAfterMother = false;
		}
		body.setTakesAfterMother(takesAfterMother); // Even if race is inherited from father, can still take most features from mother

		// Non-biped parents:
		// These are true if the leg config could be applied, but wasn't from the roll
		boolean motherLegConfigSkipped = false;
		boolean fatherLegConfigSkipped = false;
		if(takesAfterMother) {
			if(body.getLeg().getType().isLegConfigurationAvailable(motherBody.getLegConfiguration())) {
				if (Util.random.nextInt(100) < 75) {
					body.getLeg().setLegConfigurationForced(body.getLeg().getType(), motherBody.getLegConfiguration());
				} else {motherLegConfigSkipped = true;}
			}
		} else {
			if(body.getLeg().getType().isLegConfigurationAvailable(fatherBody.getLegConfiguration())) {
				if (Util.random.nextInt(100) < 75) {
					body.getLeg().setLegConfigurationForced(body.getLeg().getType(), fatherBody.getLegConfiguration());
				} else {fatherLegConfigSkipped = true;}
			}
		}

		float takesAfterMotherChance = takesAfterMother?0.75f:0.25f;

		List<AbstractBodyCoveringType> typesToInfluence = new ArrayList<>();
		// Skin & fur colours:
		for(BodyPartInterface bp : body.getAllBodyParts()) {
			if(bp.getBodyCoveringType(body)!=null && !(bp instanceof Eye)) { // Exclude eye as that's covered below
				typesToInfluence.add(bp.getBodyCoveringType(body));
			}
		}
		typesToInfluence.add(BodyCoveringType.ANUS);
		typesToInfluence.add(BodyCoveringType.NIPPLES);
		typesToInfluence.add(BodyCoveringType.MOUTH);
		typesToInfluence.add(BodyCoveringType.TONGUE);

		if(raceFromMother && (takesAfterMother || body.getRace()!=fatherBody.getRace())) {
			typesToInfluence = setCoveringColours(body, motherBody, typesToInfluence);
			setCoveringColours(body, fatherBody, typesToInfluence);
		} else {
			typesToInfluence = setCoveringColours(body, fatherBody, typesToInfluence);
			setCoveringColours(body, motherBody, typesToInfluence);
		}

		body.updateCoverings(false, false, true, false);

		// Iris colour:
		if(Math.random()<=0.9f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getCoverings().put(body.getEye().getBodyCoveringType(body),
						new Covering(body.getEye().getBodyCoveringType(body), motherBody.getCoveringFromType(motherBody.getEye()).getPattern(),
								motherBody.getCoveringFromType(motherBody.getEye()).getPrimaryColour(), motherBody.getCoveringFromType(motherBody.getEye()).isPrimaryGlowing(),
								motherBody.getCoveringFromType(motherBody.getEye()).getSecondaryColour(), motherBody.getCoveringFromType(motherBody.getEye()).isSecondaryGlowing()));
			} else {
				body.getCoverings().put(body.getEye().getBodyCoveringType(body),
						new Covering(body.getEye().getBodyCoveringType(body), fatherBody.getCoveringFromType(fatherBody.getEye()).getPattern(),
								fatherBody.getCoveringFromType(fatherBody.getEye()).getPrimaryColour(), fatherBody.getCoveringFromType(fatherBody.getEye()).isPrimaryGlowing(),
								fatherBody.getCoveringFromType(fatherBody.getEye()).getSecondaryColour(), fatherBody.getCoveringFromType(fatherBody.getEye()).isSecondaryGlowing()));
			}
		}

		// Pupil colour:
		if(Math.random()<=0.5f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getCoverings().put(BodyCoveringType.EYE_PUPILS,
						new Covering(body.getEye().getBodyCoveringType(body), motherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getPattern(),
								motherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getPrimaryColour(), motherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).isPrimaryGlowing(),
								motherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getSecondaryColour(), motherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).isSecondaryGlowing()));
			} else {
				body.getCoverings().put(BodyCoveringType.EYE_PUPILS,
						new Covering(body.getEye().getBodyCoveringType(body), fatherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getPattern(),
								fatherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getPrimaryColour(), fatherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).isPrimaryGlowing(),
								fatherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).getSecondaryColour(), fatherBody.getCovering(BodyCoveringType.EYE_PUPILS, false).isSecondaryGlowing()));
			}
		}
		// Sclera colour:
		if(Math.random()<=0.5f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getCoverings().put(BodyCoveringType.EYE_SCLERA,
						new Covering(body.getEye().getBodyCoveringType(body), motherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getPattern(),
								motherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getPrimaryColour(), motherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).isPrimaryGlowing(),
								motherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getSecondaryColour(), motherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).isSecondaryGlowing()));
			} else {
				body.getCoverings().put(BodyCoveringType.EYE_SCLERA,
						new Covering(body.getEye().getBodyCoveringType(body), fatherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getPattern(),
								fatherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getPrimaryColour(), fatherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).isPrimaryGlowing(),
								fatherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).getSecondaryColour(), fatherBody.getCovering(BodyCoveringType.EYE_SCLERA, false).isSecondaryGlowing()));
			}
		}

		// Body core:
		// Height:
		if(body.getHeightValue()>=Height.getShortStatureCutOff()) {
//					System.out.println("height adjusted");
			body.setHeight(getSizeFromGenetics(
					body.getHeightValue(),
					(body.isFeminine()?motherBody.isFeminine():!motherBody.isFeminine()),
					motherBody.getHeightValue(),
					(body.isFeminine()?fatherBody.isFeminine():!fatherBody.isFeminine()),
					fatherBody.getHeightValue()));
		}

		// Femininity:
		switch(startingGender.getType()) {
			case FEMININE:
				if(takesAfterMother) {
					if(motherBody.getFemininity()>=Femininity.FEMININE.getMinimumFemininity()) {
						body.setFemininity(motherBody.getFemininity());
					}
				} else {
					if(fatherBody.getFemininity()>=Femininity.FEMININE.getMinimumFemininity()) {
						body.setFemininity(fatherBody.getFemininity());
					}
				}
				break;
			case NEUTRAL:
				if(takesAfterMother) {
					if(Femininity.valueOf(motherBody.getFemininity())==Femininity.ANDROGYNOUS) {
						body.setFemininity(motherBody.getFemininity());
					}
				} else {
					if(Femininity.valueOf(fatherBody.getFemininity())==Femininity.ANDROGYNOUS) {
						body.setFemininity(fatherBody.getFemininity());
					}
				}
				break;
			case MASCULINE:
				if(takesAfterMother) {
					if(motherBody.getFemininity()<Femininity.ANDROGYNOUS.getMinimumFemininity()) {
						body.setFemininity(motherBody.getFemininity());
					}
				} else {
					if(fatherBody.getFemininity()<Femininity.ANDROGYNOUS.getMinimumFemininity()) {
						body.setFemininity(fatherBody.getFemininity());
					}
				}
				break;
		}

		// Body size:
		int minimumSize = Math.min(motherBody.getBodySize(), fatherBody.getBodySize()) - Util.random.nextInt(5);
		int maximumSize = Math.min(motherBody.getBodySize(), fatherBody.getBodySize()) + Util.random.nextInt(5);
		if(takesAfterMother) {
			minimumSize = Math.max(minimumSize, (feminineGender?motherGenericBody.getFemaleBodySize()-30:motherGenericBody.getMaleBodySize()-30));
			maximumSize = Math.max(maximumSize, (feminineGender?motherGenericBody.getFemaleBodySize()+30:motherGenericBody.getMaleBodySize()+30));
		} else {
			minimumSize = Math.max(minimumSize, (feminineGender?fatherGenericBody.getFemaleBodySize()-30:fatherGenericBody.getMaleBodySize()-30));
			maximumSize = Math.max(maximumSize, (feminineGender?fatherGenericBody.getFemaleBodySize()+30:fatherGenericBody.getMaleBodySize()+30));
		}
		int variance = (maximumSize==minimumSize?0:Util.random.nextInt(maximumSize-minimumSize));
		body.setBodySize(minimumSize + variance);

		// Muscle:
		int minimumMuscle = Math.min(motherBody.getMuscle(), fatherBody.getMuscle()) - Util.random.nextInt(5);
		int maximumMuscle = Math.min(motherBody.getMuscle(), fatherBody.getMuscle()) + Util.random.nextInt(5);
		if(takesAfterMother) {
			minimumMuscle = Math.max(minimumMuscle, (feminineGender?motherGenericBody.getFemaleMuscle()-30:motherGenericBody.getMaleMuscle()-30));
			maximumMuscle = Math.max(maximumMuscle, (feminineGender?motherGenericBody.getFemaleMuscle()+30:motherGenericBody.getMaleMuscle()+30));
		} else {
			minimumMuscle = Math.max(minimumMuscle, (feminineGender?fatherGenericBody.getFemaleMuscle()-30:fatherGenericBody.getMaleMuscle()-30));
			maximumMuscle = Math.max(maximumMuscle, (feminineGender?fatherGenericBody.getFemaleMuscle()+30:fatherGenericBody.getMaleMuscle()+30));
		}
		variance = (maximumMuscle==minimumMuscle?0:Util.random.nextInt(maximumMuscle-minimumMuscle));
		body.setMuscle(minimumMuscle + variance);


		// Body parts:

		boolean inheritsFromMotherFemininity = motherBody.isFeminine() == body.isFeminine();
		boolean inheritsFromFatherFemininity = fatherBody.isFeminine() == body.isFeminine();

		// Arm:
		if(Math.random()>0.75) {
			body.getArm().setArmRows(blankNPC, takesAfterMother?motherBody.getArm().getArmRows():fatherBody.getArm().getArmRows());
		}

		// Ass:
		// Ass size:
		body.getAss().setAssSize(blankNPC, getSizeFromGenetics(
				body.getAss().getAssSize().getValue(),
				inheritsFromMotherFemininity, motherBody.getAss().getAssSize().getValue(),
				inheritsFromFatherFemininity, fatherBody.getAss().getAssSize().getValue()));
		// Hip size:
		body.getAss().setHipSize(blankNPC, getSizeFromGenetics(
				body.getAss().getHipSize().getValue(),
				inheritsFromMotherFemininity, motherBody.getAss().getHipSize().getValue(),
				inheritsFromFatherFemininity, fatherBody.getAss().getHipSize().getValue()));


		// Breasts:
		boolean inheritsFromMotherBreasts = motherBody.hasBreasts();
		boolean inheritsFromFatherBreasts = fatherBody.hasBreasts();
		if(body.getBreast().getRawSizeValue()>0) {
			// Breast shape:
			if(Math.random()>=0.8f) {
				if(inheritsFromMotherBreasts && inheritsFromFatherBreasts) {
					if(Math.random()>=takesAfterMotherChance) {
						body.getBreast().setShape(blankNPC, motherBody.getBreast().getShape());
					} else {
						body.getBreast().setShape(blankNPC, fatherBody.getBreast().getShape());
					}
				} else if(inheritsFromMotherBreasts) {
					body.getBreast().setShape(blankNPC, motherBody.getBreast().getShape());
				} else if(inheritsFromFatherBreasts) {
					body.getBreast().setShape(blankNPC, fatherBody.getBreast().getShape());
				}
			}
			// Breast size:
			body.getBreast().setSize(blankNPC, getSizeFromGenetics(
					body.getBreast().getSize().getMeasurement(),
					inheritsFromMotherBreasts, motherBody.getBreast().getSize().getMeasurement(),
					inheritsFromFatherBreasts, fatherBody.getBreast().getSize().getMeasurement()));
			// Breast rows:
			if(Math.random()>=0.75) {
				if(Math.random()>=takesAfterMotherChance) {
					body.getBreast().setRows(blankNPC, motherBody.getBreast().getRows());
				} else {
					body.getBreast().setRows(blankNPC, fatherBody.getBreast().getRows());
				}
			}
			// Modifiers:
			for(OrificeModifier om : OrificeModifier.values()) {
				if(Math.random()>=0.5) {
					if(inheritsFromMotherBreasts && inheritsFromFatherBreasts) {
						if(Math.random()>=takesAfterMotherChance) {
							if(motherBody.getBreast().getNipples().getOrificeNipples().hasOrificeModifier(om)) {
								body.getBreast().getNipples().getOrificeNipples().addOrificeModifier(blankNPC, om);
							}
						} else {
							if(fatherBody.getBreast().getNipples().getOrificeNipples().hasOrificeModifier(om)) {
								body.getBreast().getNipples().getOrificeNipples().addOrificeModifier(blankNPC, om);
							}
						}
					} else if(inheritsFromMotherBreasts) {
						if(motherBody.getBreast().getNipples().getOrificeNipples().hasOrificeModifier(om)) {
							body.getBreast().getNipples().getOrificeNipples().addOrificeModifier(blankNPC, om);
						}
					} else if(inheritsFromFatherBreasts) {
						if(fatherBody.getBreast().getNipples().getOrificeNipples().hasOrificeModifier(om)) {
							body.getBreast().getNipples().getOrificeNipples().addOrificeModifier(blankNPC, om);
						}
					}
				}
			}
		}
		// Nipple count:
		if(Math.random()>0.75f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getBreast().setNippleCountPerBreast(blankNPC, motherBody.getBreast().getNippleCountPerBreast());
			} else {
				body.getBreast().setNippleCountPerBreast(blankNPC, fatherBody.getBreast().getNippleCountPerBreast());
			}
		}
		// Nipple shape:
		if(Math.random()>=0.75f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getBreast().getNipples().setNippleShape(blankNPC, motherBody.getBreast().getNipples().getNippleShape());
			} else {
				body.getBreast().getNipples().setNippleShape(blankNPC, fatherBody.getBreast().getNipples().getNippleShape());
			}
		}
		// Areolae shape:
		if(Math.random()>=0.75f) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getBreast().getNipples().setAreolaeShape(blankNPC, motherBody.getBreast().getNipples().getAreolaeShape());
			} else {
				body.getBreast().getNipples().setAreolaeShape(blankNPC, fatherBody.getBreast().getNipples().getAreolaeShape());
			}
		}
		// Nipple size:
		body.getBreast().getNipples().setNippleSize(blankNPC, getSizeFromGenetics(
				body.getBreast().getNipples().getNippleSizeValue(),
				inheritsFromMotherBreasts, motherBody.getBreast().getNipples().getNippleSize().getValue(),
				inheritsFromFatherBreasts, fatherBody.getBreast().getNipples().getNippleSize().getValue()));
		// Areolae size:
		body.getBreast().getNipples().setAreolaeSize(blankNPC, getSizeFromGenetics(
				body.getBreast().getNipples().getAreolaeSizeValue(),
				inheritsFromMotherBreasts, motherBody.getBreast().getNipples().getAreolaeSize().getValue(),
				inheritsFromFatherBreasts, fatherBody.getBreast().getNipples().getAreolaeSize().getValue()));

		// Face:
		// Lip size:
		body.getFace().getMouth().setLipSize(blankNPC, getSizeFromGenetics(
				body.getFace().getMouth().getLipSizeValue(),
				inheritsFromMotherFemininity, motherBody.getFace().getMouth().getLipSizeValue(),
				inheritsFromFatherFemininity, fatherBody.getFace().getMouth().getLipSizeValue()));
		// Mouth modifiers:
		for(OrificeModifier om : OrificeModifier.values()) {
			if(Math.random()>=0.5) {
				if(Math.random()>=takesAfterMotherChance) {
					if(motherBody.getFace().getMouth().getOrificeMouth().hasOrificeModifier(om)) {
						body.getFace().getMouth().getOrificeMouth().addOrificeModifier(blankNPC, om);
					}
				} else {
					if(fatherBody.getFace().getMouth().getOrificeMouth().hasOrificeModifier(om)) {
						body.getFace().getMouth().getOrificeMouth().addOrificeModifier(blankNPC, om);
					}
				}
			}
		}
		// Tongue modifiers:
		for(TongueModifier tm : TongueModifier.values()) {
			if(Math.random()>=0.5) {
				if(Math.random()>=takesAfterMotherChance) {
					if(motherBody.getFace().getTongue().hasTongueModifier(tm)) {
						body.getFace().getTongue().addTongueModifier(blankNPC, tm);
					}
				} else {
					if(fatherBody.getFace().getTongue().hasTongueModifier(tm)) {
						body.getFace().getTongue().addTongueModifier(blankNPC, tm);
					}
				}
			}
		}

		// Eyes
		// Eye pairs:
		if(Math.random()>=0.75) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getEye().setEyePairs(blankNPC, motherBody.getEye().getEyePairs());
			} else {
				body.getEye().setEyePairs(blankNPC, fatherBody.getEye().getEyePairs());
			}
		}
		// Iris shape:
		if(Math.random()>=0.75) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getEye().setIrisShape(blankNPC, motherBody.getEye().getIrisShape());
			} else {
				body.getEye().setIrisShape(blankNPC, fatherBody.getEye().getIrisShape());
			}
		}
		// Pupil shape:
		if(Math.random()>=0.75) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getEye().setPupilShape(blankNPC, motherBody.getEye().getPupilShape());
			} else {
				body.getEye().setPupilShape(blankNPC, fatherBody.getEye().getPupilShape());
			}
		}

		// Horn:
		// Horn rows:
		if(Math.random()>=0.75) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getHorn().setHornRows(blankNPC, motherBody.getHorn().getHornRows());
			} else {
				body.getHorn().setHornRows(blankNPC, fatherBody.getHorn().getHornRows());
			}
		}

		// Horn type and length:
		if (body.getHornType() == HornType.NONE) {
			if (Math.random() >= takesAfterMotherChance) {
				if (motherBody.hasGenericHorns()) {
					body.getHorn().setTypeAndLength(motherBody.getHornType(), motherBody.getHorn().getHornLengthValue());
				}
			} else {
				if (fatherBody.hasGenericHorns()) {
					body.getHorn().setTypeAndLength(fatherBody.getHornType(), fatherBody.getHorn().getHornLengthValue());
				}
			}
		}

		// Penis:
		boolean inheritsFromMotherPenis = motherBody.hasPenis();
		boolean inheritsFromFatherPenis = fatherBody.hasPenis();
		if(body.getPenis().getType()!=PenisType.NONE) {
			// Penis size:
			body.getPenis().setPenisLength(blankNPC, getSizeFromGenetics(
					body.getPenis().getRawLengthValue(),
					inheritsFromMotherPenis, motherBody.getPenis().getRawLengthValue(),
					inheritsFromFatherPenis, fatherBody.getPenis().getRawLengthValue()));
			// Penis modifiers:
			for(PenetrationModifier pm : PenetrationModifier.values()) {
				if(Math.random()>=0.5) {
					if(inheritsFromMotherPenis && inheritsFromFatherPenis) {
						if(Math.random()>=takesAfterMotherChance) {
							if(motherBody.getPenis().hasPenisModifier(pm)) {
								body.getPenis().addPenisModifier(blankNPC, pm);
							}
						} else {
							if(fatherBody.getPenis().hasPenisModifier(pm)) {
								body.getPenis().addPenisModifier(blankNPC, pm);
							}
						}
					} else if(inheritsFromMotherPenis) {
						if(motherBody.getPenis().hasPenisModifier(pm)) {
							body.getPenis().addPenisModifier(blankNPC, pm);
						}
					} else if(inheritsFromFatherPenis) {
						if(fatherBody.getPenis().hasPenisModifier(pm)) {
							body.getPenis().addPenisModifier(blankNPC, pm);
						}
					}
				}
			}
			// Urethra modifiers:
			for(OrificeModifier om : OrificeModifier.values()) {
				if(Math.random()>=0.5) {
					if(inheritsFromMotherPenis && inheritsFromFatherPenis) {
						if(Math.random()>=takesAfterMotherChance) {
							if(motherBody.getPenis().getOrificeUrethra().hasOrificeModifier(om)) {
								body.getPenis().getOrificeUrethra().addOrificeModifier(blankNPC, om);
							}
						} else {
							if(fatherBody.getPenis().getOrificeUrethra().hasOrificeModifier(om)) {
								body.getPenis().getOrificeUrethra().addOrificeModifier(blankNPC, om);
							}
						}
					} else if(inheritsFromMotherPenis) {
						if(motherBody.getPenis().getOrificeUrethra().hasOrificeModifier(om)) {
							body.getPenis().getOrificeUrethra().addOrificeModifier(blankNPC, om);
						}
					} else if(inheritsFromFatherPenis) {
						if(fatherBody.getPenis().getOrificeUrethra().hasOrificeModifier(om)) {
							body.getPenis().getOrificeUrethra().addOrificeModifier(blankNPC, om);
						}
					}
				}
			}
			// Testicles:
			// Testicle size:
			body.getPenis().getTesticle().setTesticleSize(blankNPC, getSizeFromGenetics(
					body.getPenis().getTesticle().getTesticleSize().getValue(),
					inheritsFromMotherPenis, motherBody.getPenis().getTesticle().getTesticleSize().getValue(),
					inheritsFromFatherPenis, fatherBody.getPenis().getTesticle().getTesticleSize().getValue()));
			// Testicle count:
			if(Math.random()>=0.75) {
				if(inheritsFromMotherPenis && inheritsFromFatherPenis) {
					if(Math.random()>=takesAfterMotherChance) {
						body.getPenis().getTesticle().setTesticleCount(blankNPC, motherBody.getPenis().getTesticle().getTesticleCount());
					} else {
						body.getPenis().getTesticle().setTesticleCount(blankNPC, fatherBody.getPenis().getTesticle().getTesticleCount());
					}
				} else if(inheritsFromMotherPenis) {
					body.getPenis().getTesticle().setTesticleCount(blankNPC, motherBody.getPenis().getTesticle().getTesticleCount());
				} else if(inheritsFromFatherPenis) {
					body.getPenis().getTesticle().setTesticleCount(blankNPC, fatherBody.getPenis().getTesticle().getTesticleCount());
				}
			}
			// Internal testicles:
			if(body.getVagina().getType()!=VaginaType.NONE && !Main.game.isFutanariTesticlesEnabled()) {
				body.getPenis().getTesticle().setInternal(blankNPC, true);

			} else if(Math.random()>=0.75) {
				if(inheritsFromMotherPenis && inheritsFromFatherPenis) {
					if(Math.random()>=takesAfterMotherChance) {
						if(motherBody.getPenis().getTesticle().isInternal(null)) {
							body.getPenis().getTesticle().setInternal(blankNPC, true);
						}
					} else {
						if(fatherBody.getPenis().getTesticle().isInternal(null)) {
							body.getPenis().getTesticle().setInternal(blankNPC, true);
						}
					}
				} else if(inheritsFromMotherPenis) {
					if(motherBody.getPenis().getTesticle().isInternal(null)) {
						body.getPenis().getTesticle().setInternal(blankNPC, true);
					}
				} else if(inheritsFromFatherPenis) {
					if(fatherBody.getPenis().getTesticle().isInternal(null)) {
						body.getPenis().getTesticle().setInternal(blankNPC, true);
					}
				}
			}
			// Cum Production:
			body.getPenis().getTesticle().setCumStorage(blankNPC, getSizeFromGenetics(
					body.getPenis().getTesticle().getRawCumStorageValue(),
					inheritsFromMotherPenis, motherBody.getPenis().getTesticle().getRawCumStorageValue(),
					inheritsFromFatherPenis, fatherBody.getPenis().getTesticle().getRawCumStorageValue()));
		}

		// Wings:
		if(!body.getArm().getType().allowsFlight() && body.getWing().getType() == WingType.NONE) { // Do not give back wings to characters who have arm wings.
			if (Math.random()>=takesAfterMotherChance) {
				if (motherBody.hasGenericWings()) {
					body.getWing().setTypeAndSize(motherBody.getWingType(), motherBody.getWingSizeValue());
				}
			} else {
				if (fatherBody.hasGenericWings()) {
					body.getWing().setTypeAndSize(fatherBody.getWingType(), fatherBody.getWingSizeValue());
				}
			}
		}

		// Tail:
		if(Math.random()>0.75) {
			if(Math.random()>=takesAfterMotherChance) {
				body.getTail().setTailCount(blankNPC, motherBody.getTail().getTailCount(), false);
			} else {
				body.getTail().setTailCount(blankNPC, fatherBody.getTail().getTailCount(), false);
			}
		}

		// Vagina:
		boolean inheritsFromMotherVagina = motherBody.hasVagina();
		boolean inheritsFromFatherVagina = fatherBody.hasVagina();
		if(body.getVagina().getType()!=VaginaType.NONE) {
			// Clitoris size:
			body.getVagina().getClitoris().setClitorisSize(blankNPC, getSizeFromGenetics(
					body.getVagina().getClitoris().getRawClitorisSizeValue(),
					inheritsFromMotherVagina, motherBody.getVagina().getClitoris().getRawClitorisSizeValue(),
					inheritsFromFatherVagina, fatherBody.getVagina().getClitoris().getRawClitorisSizeValue()));
			//TODO clit girth and clit modifiers
			// Labia size:
			body.getVagina().setLabiaSize(blankNPC, getSizeFromGenetics(
					body.getVagina().getRawLabiaSizeValue(),
					inheritsFromMotherVagina, motherBody.getVagina().getRawLabiaSizeValue(),
					inheritsFromFatherVagina, fatherBody.getVagina().getRawLabiaSizeValue()));
//					// Capacity:
//					body.getVagina().getOrificeVagina().setCapacity(blankNPC, getSizeFromGenetics(
//							(int) body.getVagina().getOrificeVagina().getRawCapacityValue(),
//							inheritsFromMotherVagina, (int) mother.getVaginaRawCapacityValue(),
//							inheritsFromFatherVagina, (int) father.getVaginaRawCapacityValue()));
			// Wetness:
			body.getVagina().getOrificeVagina().setWetness(blankNPC, getSizeFromGenetics(
					body.getVagina().getOrificeVagina().getWetness(blankNPC).getValue(),
					inheritsFromMotherVagina, motherBody.getVagina().getOrificeVagina().getWetness(null).getValue(),
					inheritsFromFatherVagina, fatherBody.getVagina().getOrificeVagina().getWetness(null).getValue()));
			// Modifiers:
			for(OrificeModifier om : OrificeModifier.values()) {
				if(Math.random()>=0.5) {
					if(inheritsFromMotherVagina && inheritsFromFatherVagina) {
						if(Math.random()>=takesAfterMotherChance) {
							if(motherBody.getVagina().getOrificeVagina().hasOrificeModifier(om)) {
								body.getVagina().getOrificeVagina().addOrificeModifier(blankNPC, om);
							}
						} else {
							if(fatherBody.getVagina().getOrificeVagina().hasOrificeModifier(om)) {
								body.getVagina().getOrificeVagina().addOrificeModifier(blankNPC, om);
							}
						}
					} else if(inheritsFromMotherVagina) {
						if(motherBody.getVagina().getOrificeVagina().hasOrificeModifier(om)) {
							body.getVagina().getOrificeVagina().addOrificeModifier(blankNPC, om);
						}
					} else if(inheritsFromFatherVagina) {
						if(fatherBody.getVagina().getOrificeVagina().hasOrificeModifier(om)) {
							body.getVagina().getOrificeVagina().addOrificeModifier(blankNPC, om);
						}
					}
				}
			}
		}

		Boolean setToFeral = (motherBody.isFeral() && fatherBody.isFeral()) || (motherBody.isFeral() && Math.random()<0.75) || (fatherBody.isFeral() && Math.random()<0.25);
		if (motherBody.isFeral() && (fatherBody.isFeral() || (!fatherBody.isFeral() && Math.random()<=0.75))) {setToFeral=true;}
		if(setToFeral){
			if (motherLegConfigSkipped) {
				body.getLeg().setLegConfigurationForced(body.getLeg().getType(), motherBody.getLegConfiguration());
			}
			if (fatherLegConfigSkipped) {
				body.getLeg().setLegConfigurationForced(body.getLeg().getType(), fatherBody.getLegConfiguration());
			}
			if (body.getSubspecies().isFeralConfigurationAvailable(body)){
				body.setFeral(offspring, body.getSubspecies());
			}
		}

		if(!body.isFeral()
				&& (Main.getProperties().getUddersLevel()==0
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && Main.getProperties().getUddersLevel()==1)
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && body.getRaceStage()!=RaceStage.GREATER))) {
			body.getBreastCrotch().setType(null, BreastType.NONE);
		}
	}

	private static List<AbstractBodyCoveringType> setCoveringColours(Body body, Body influencingCharacterBody, List<AbstractBodyCoveringType> typesToInfluence) {
		List<AbstractBodyCoveringType> tempList = new ArrayList<>(typesToInfluence);

		// Skin & fur colours:
		for(BodyPartInterface bp : influencingCharacterBody.getAllBodyParts()){
			if(bp.getBodyCoveringType(influencingCharacterBody)!=null
					&& !(bp instanceof Eye)) {
				if(tempList.contains(bp.getBodyCoveringType(influencingCharacterBody))) {
					Covering covering = influencingCharacterBody.getCovering(bp.getBodyCoveringType(influencingCharacterBody), false);
					body.getCoverings().put(
							bp.getBodyCoveringType(influencingCharacterBody),
							new Covering(covering.getType(), covering.getPattern(), covering.getModifier(), covering.getPrimaryColour(), covering.isPrimaryGlowing(), covering.getSecondaryColour(), covering.isSecondaryGlowing()));
					tempList.remove(bp.getBodyCoveringType(influencingCharacterBody));
//					System.out.println("Set: "+bp.getType().getName(character)+" : "+bp.getBodyCoveringType().getName(character)+"("+bp.getType().getRace().getName()+") : "+covering.getPrimaryColour().getName());
				}
			}
		}

		List<AbstractBodyCoveringType> extraCoverings = new ArrayList<>();
		extraCoverings.add(BodyCoveringType.ANUS);
		extraCoverings.add(BodyCoveringType.NIPPLES);
		extraCoverings.add(BodyCoveringType.MOUTH);
		extraCoverings.add(BodyCoveringType.TONGUE);

		for(AbstractBodyCoveringType bct : extraCoverings) {
			if(tempList.contains(bct)) {
				Covering covering = influencingCharacterBody.getCovering(bct, false);
				body.getCoverings().put(
						bct,
						new Covering(covering.getType(), covering.getPattern(), covering.getModifier(), covering.getPrimaryColour(), covering.isPrimaryGlowing(), covering.getSecondaryColour(), covering.isSecondaryGlowing()));
				tempList.remove(bct);
			}
//				System.out.println("Set: "+bct+" : "+bct.getName(character)+" : "+covering.getPrimaryColour().getName());
		}
//		System.out.println("------------------------------");

		return tempList;
	}

	private static int getSizeFromGenetics(int baseSize, boolean inheritsFromMother, int motherSize, boolean inheritsFromFather, int fatherSize) {
		// $BaseRaceSize + RandomAmount($ParentSize - $BaseRaceSize), then +-10%
		int variation = 0;
		if(inheritsFromMother && inheritsFromFather) {
			variation = (motherSize + fatherSize)/2;
		} else if(inheritsFromMother) {
			variation = motherSize;
		} else if(inheritsFromFather) {
			variation = fatherSize;
		}

		if(variation != 0) {
			int difference = variation - baseSize;
			return (int) Math.round(baseSize + difference*Math.random());
		} else {
			return baseSize;
		}

//		return (int) ((baseSize + (Math.signum(difference)*Util.random.nextInt(Math.abs(difference) +1)))*(0.9f+(Math.random()*0.2f)));
	}

	public Body generateHalfDemonBody(GameCharacter linkedCharacter, Gender startingGender, AbstractSubspecies halfSubspecies, boolean applyHalfDemonAttributeChanges) {
		return generateHalfDemonBody(linkedCharacter, startingGender, halfSubspecies, applyHalfDemonAttributeChanges, null);
	}

	public Body generateHalfDemonBody(GameCharacter linkedCharacter, Gender startingGender, AbstractSubspecies halfSubspecies, boolean applyHalfDemonAttributeChanges, RaceStage overrideStage) {
//		Gender startingGender;
		if(startingGender==null) {
			startingGender = Math.random()>0.5f?Gender.F_V_B_FEMALE:Gender.M_P_MALE;
		}
		RaceStage stage = overrideStage!=null?overrideStage:getRaceStageFromPreferences(Main.getProperties().getSubspeciesFeminineFurryPreferencesMap().get(halfSubspecies), startingGender, halfSubspecies);
		AbstractRacialBody demonBody = RacialBody.DEMON;

		if(linkedCharacter!=null) {
//			startingGender = linkedCharacter.getGender();
//			stage = linkedCharacter.getRaceStage();

			if(applyHalfDemonAttributeChanges) {
				if(linkedCharacter.getAttributeValue(Attribute.MAJOR_CORRUPTION)<75) {
					linkedCharacter.setAttribute(Attribute.MAJOR_CORRUPTION, 75+Util.random.nextInt(26));
				}
				if(linkedCharacter.getAttributeValue(Attribute.MAJOR_ARCANE)<25) {
					linkedCharacter.setAttribute(Attribute.MAJOR_ARCANE, 25+Util.random.nextInt(11));
				}
			}
		}

		switch(stage) {
			case FERAL:
			case GREATER:
			case LESSER:
				break;
			// Anything less than lesser is covered by demon parts, so make it up to lesser:
			case PARTIAL_FULL:
			case PARTIAL:
			case HUMAN:
				stage = RaceStage.LESSER;
				break;
		}
		Body body = generateBody(linkedCharacter, startingGender, halfSubspecies, stage);
		body.setSubspeciesOverride(Subspecies.HALF_DEMON);

		body.setAss(new Ass(AssType.DEMON_COMMON,
				(startingGender.isFeminine() ? demonBody.getFemaleAssSize() : demonBody.getMaleAssSize()),
				(startingGender.isFeminine() ? demonBody.getFemaleHipSize() : demonBody.getMaleHipSize()),
				demonBody.getAnusWetness(),
				demonBody.getAnusCapacity(),
				demonBody.getAnusDepth(),
				demonBody.getAnusElasticity(),
				demonBody.getAnusPlasticity(),
				true));

		body.setBreast(new Breast(BreastType.DEMON_COMMON,
				Util.randomItemFrom(demonBody.getBreastShapes()),
				(startingGender.getGenderName().isHasBreasts()
						? Math.max(CupSize.getMinimumCupSizeForBreasts().getMeasurement(), demonBody.getBreastSize()+Main.getProperties().breastSizePreference)
						: demonBody.getNoBreastSize()),
				(startingGender.isFeminine() ? demonBody.getFemaleLactationRate() : demonBody.getMaleLactationRate()),
				(startingGender.isFeminine() ? demonBody.getBreastCountFemale() : demonBody.getBreastCountMale()),
				(startingGender.isFeminine() ? demonBody.getFemaleNippleSize() : demonBody.getMaleNippleSize()),
				(startingGender.isFeminine() ? demonBody.getFemaleNippleShape() : demonBody.getMaleNippleShape()),
				(startingGender.isFeminine() ? demonBody.getFemaleAreolaeSize() : demonBody.getMaleAreolaeSize()),
				(startingGender.isFeminine() ? demonBody.getFemaleAreolaeShape() : demonBody.getMaleAreolaeShape()),
				(startingGender.isFeminine() ? demonBody.getFemaleNippleCountPerBreast() : demonBody.getMaleNippleCountPerBreast()),
				(startingGender.isFeminine() ? demonBody.getFemaleBreastCapacity() : demonBody.getMaleBreastCapacity()),
				(startingGender.isFeminine() ? demonBody.getFemaleBreastDepth() : demonBody.getMaleBreastDepth()),
				(startingGender.isFeminine() ? demonBody.getFemaleBreastElasticity() : demonBody.getMaleBreastElasticity()),
				(startingGender.isFeminine() ? demonBody.getFemaleBreastPlasticity() : demonBody.getMaleBreastPlasticity()),
				true));

		body.setBreastCrotch(
				new BreastCrotch(startingGender.isFeminine()
						?demonBody.getBreastCrotchType()
						:BreastType.NONE,
						Util.randomItemFrom(demonBody.getBreastCrotchShapes()),
						demonBody.getBreastCrotchSize()+Main.getProperties().udderSizePreference,
						demonBody.getBreastCrotchLactationRate(),
						demonBody.getBreastCrotchCount(),
						demonBody.getBreastCrotchNippleSize(),
						demonBody.getBreastCrotchNippleShape(),
						demonBody.getBreastCrotchAreolaeSize(),
						demonBody.getBreastCrotchAreolaeShape(),
						demonBody.getNippleCountPerBreastCrotch(),
						demonBody.getBreastCrotchCapacity(),
						demonBody.getBreastCrotchDepth(),
						demonBody.getBreastCrotchElasticity(),
						demonBody.getBreastCrotchPlasticity(),
						true));

		if(!body.isFeral()
				&& (Main.getProperties().getUddersLevel()==0
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && Main.getProperties().getUddersLevel()==1)
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && body.getRaceStage()!=RaceStage.GREATER))) {
			body.getBreastCrotch().setType(null, BreastType.NONE);
		}

		if(halfSubspecies==Subspecies.HUMAN) {
			body.setEar(new Ear(EarType.DEMON_COMMON));
		}

		body.setEye(new Eye(EyeType.DEMON_COMMON));

		if(halfSubspecies==Subspecies.HUMAN) {
			body.setHair(new Hair(HairType.DEMON,
					(startingGender.isFeminine() ? demonBody.getFemaleHairLength() : demonBody.getMaleHairLength()),
					HairStyle.getRandomHairStyle(body.isFeminine(), (startingGender.isFeminine() ? demonBody.getFemaleHairLength() : demonBody.getMaleHairLength())),
					stage));
		}

		body.setHorn(new Horn(demonBody.getRandomHornType(false),
				(startingGender.isFeminine() ? demonBody.getFemaleHornLength() : demonBody.getMaleHornLength())));

		body.setPenis(startingGender.getGenderName().isHasPenis()
				? new Penis(demonBody.getPenisType(),
				demonBody.getPenisSize(),
				true,
				demonBody.getPenisGirth(),
				demonBody.getTesticleSize(),
				demonBody.getCumProduction(),
				demonBody.getTesticleQuantity())
				: new Penis(PenisType.NONE, 0, false, 0, 0, 0, 2));
		// If non-human, set modifiers to be the same as the default race modifiers:
		if(halfSubspecies!=Subspecies.HUMAN) {
			body.getPenis().clearPenisModifiers();
			for(PenetrationModifier mod : RacialBody.valueOfRace(halfSubspecies.getRace()).getPenisType().getDefaultRacialPenetrationModifiers()) {
				body.getPenis().addPenisModifier(linkedCharacter, mod);
			}
		}

		if(body.getPenis().getType()!=PenisType.NONE
				&& body.getPenis().getType()!=PenisType.DILDO
				&& body.getVagina().getType()!=VaginaType.NONE
				&& body.getVagina().getType()!=VaginaType.ONAHOLE
				&& !Main.game.isFutanariTesticlesEnabled()) {
			body.getPenis().getTesticle().setInternal(null, true);
		}


		List<AbstractTailType> tailTypes = RacialBody.valueOfRace(halfSubspecies.getRace()).getTailType();
		if(tailTypes.size()==1 && tailTypes.get(0)==TailType.NONE) {
			body.setTail(new Tail(demonBody.getRandomTailType(false)));
		}

		body.setVagina(startingGender.getGenderName().isHasVagina()
				? new Vagina(demonBody.getVaginaType(),
				LabiaSize.getRandomLabiaSize().getValue(),
				demonBody.getClitSize(),
				demonBody.getClitGirth(),
				demonBody.getVaginaWetness(),
				demonBody.getVaginaCapacity(),
				demonBody.getVaginaDepth(),
				demonBody.getVaginaElasticity(),
				demonBody.getVaginaPlasticity(),
				true)
				: new Vagina(VaginaType.NONE, 0, 0, 0, 0, 0, 2, 3, 3, true));
		// If non-human, set modifiers to be the same as the default race modifiers:
		if(halfSubspecies!=Subspecies.HUMAN) {
			body.getVagina().getOrificeVagina().clearOrificeModifiers();
			for(OrificeModifier mod : RacialBody.valueOfRace(halfSubspecies.getRace()).getVaginaType().getDefaultRacialOrificeModifiers()) {
				body.getVagina().getOrificeVagina().addOrificeModifier(linkedCharacter, mod);
			}
			body.getVagina().setEggLayer(null, RacialBody.valueOfRace(halfSubspecies.getRace()).getVaginaType().isEggLayer());
		}

		if(!body.getArm().getType().allowsFlight()) { // Do not give back wings to characters who have arm wings.
			body.setWing(new Wing(demonBody.getRandomWingType(false),
					(startingGender.isFeminine() ? demonBody.getFemaleWingSize() : demonBody.getMaleWingSize())));
		}

//		startingBodyType.getBodyMaterial(),
//		startingBodyType.getGenitalArrangement(),
		body.setHeight((startingGender.isFeminine() ? demonBody.getFemaleHeight() : demonBody.getMaleHeight()));

		body.setFemininity(startingGender.getType()==PronounType.NEUTRAL?50:(startingGender.isFeminine() ? demonBody.getFemaleFemininity() : demonBody.getMaleFemininity()));

		body.setBodySize((startingGender.isFeminine() ? demonBody.getFemaleBodySize() : demonBody.getMaleBodySize()));

		body.setMuscle((startingGender.isFeminine() ? demonBody.getFemaleMuscle() : demonBody.getMaleMuscle()));

		body.updateCoverings(true, true, true, true);

		halfSubspecies.getRace().applyRaceChanges(body);
		halfSubspecies.applySpeciesChanges(body);

		setBodyHair(body);

		return body;
	}

	public Body generateBody(GameCharacter linkedCharacter, Gender startingGender, AbstractSubspecies species, RaceStage stage) {
		return generateBody(linkedCharacter, startingGender, RacialBody.valueOfRace(species.getRace()), species, stage);
	}

	public Body generateBody(GameCharacter linkedCharacter, Gender startingGender, AbstractRacialBody startingBodyType, RaceStage stage) {
		return generateBody(linkedCharacter, startingGender, startingBodyType, null, stage);
	}

	public Body generateBody(GameCharacter linkedCharacter, Gender startingGender, AbstractRacialBody startingBodyType, AbstractSubspecies species, RaceStage stage) {
		boolean hasVagina = startingGender.getGenderName().isHasVagina();
		boolean hasPenis = startingGender.getGenderName().isHasPenis();
		boolean hasBreasts = startingGender.getGenderName().isHasBreasts();
		boolean isSlime = species != null && species.getRace().isMaterialRace();
		boolean isHalfDemon = species == Subspecies.HALF_DEMON;
		boolean isDoll = species == Subspecies.DOLL;

		AbstractSubspecies materialSubspecies = null;
		if (isSlime) {materialSubspecies = species; }

		if(isSlime || isHalfDemon) {
			if(linkedCharacter==null || !linkedCharacter.isUnique()) {
				List<AbstractSubspecies> slimeSubspecies = new ArrayList<>();
				for(AbstractSubspecies subspecies : Subspecies.getAllSubspecies()) {
					// Special races that slimes/half-demons do not spawn as are slimes and any Subspecies which sets an override (so demons, elementals, or Youko):
					if(!isSlime && subspecies.getSubspeciesOverridePriority()==0) {
						if(startingGender.isFeminine()) {
							for(Entry<AbstractSubspecies, FurryPreference> entry : Main.getProperties().getSubspeciesFeminineFurryPreferencesMap().entrySet()) {
								if(entry.getValue() != FurryPreference.HUMAN) {
									slimeSubspecies.add(subspecies);
								}
							}
						} else {
							for(Entry<AbstractSubspecies, FurryPreference> entry : Main.getProperties().getSubspeciesMasculineFurryPreferencesMap().entrySet()) {
								if(entry.getValue() != FurryPreference.HUMAN) {
									slimeSubspecies.add(subspecies);
								}
							}
						}
					}
				}

				if(slimeSubspecies.isEmpty()) {
					slimeSubspecies.add(Subspecies.HUMAN);
				}
				species = Util.randomItemFrom(slimeSubspecies);

				if(isHalfDemon) {
					return generateHalfDemonBody(linkedCharacter, startingGender, species, true);
				}

				if(startingGender.isFeminine()) {
					stage = getRaceStageFromPreferences(Main.getProperties().getSubspeciesFeminineFurryPreferencesMap().get(species), startingGender, species);

				} else {
					stage = getRaceStageFromPreferences(Main.getProperties().getSubspeciesMasculineFurryPreferencesMap().get(species), startingGender, species);
				}

				startingBodyType = RacialBody.valueOfRace(species.getRace());
			}
		}

		if(isDoll) { // Dolls spawn as human
			species = Subspecies.HUMAN;
		}

//		System.out.println(species+", "+stage);

		boolean furryHairCheck = stage.isFaceFurry() && startingBodyType.getFaceType().getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_FURRY) && !startingBodyType.getHairType().getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);
		boolean scalyHairCheck = stage.isFaceFurry() && startingBodyType.getFaceType().getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_SCALY) && !startingBodyType.getHairType().getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);

		Body body = new Body.BodyBuilder(
				new Arm((stage.isArmFurry()?startingBodyType.getArmType():ArmType.HUMAN), startingBodyType.getArmRows()),
				new Ass(stage.isAssFurry()?startingBodyType.getAssType():AssType.HUMAN,
						(startingGender.isFeminine() ? startingBodyType.getFemaleAssSize() : startingBodyType.getMaleAssSize()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleHipSize() : startingBodyType.getMaleHipSize()),
						startingBodyType.getAnusWetness(),
						startingBodyType.getAnusCapacity(),
						startingBodyType.getAnusDepth(),
						startingBodyType.getAnusElasticity(),
						startingBodyType.getAnusPlasticity(),
						true),
				new Breast(stage.isBreastFurry()?startingBodyType.getBreastType():BreastType.HUMAN,
						Util.randomItemFrom(startingBodyType.getBreastShapes()),
						(hasBreasts
								? Math.max(CupSize.getMinimumCupSizeForBreasts().getMeasurement(), startingBodyType.getBreastSize()+Main.getProperties().breastSizePreference)
								: startingBodyType.getNoBreastSize()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleLactationRate() : startingBodyType.getMaleLactationRate()),
						(startingGender.isFeminine() ? startingBodyType.getBreastCountFemale() : startingBodyType.getBreastCountMale()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleNippleSize() : startingBodyType.getMaleNippleSize()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleNippleShape() : startingBodyType.getMaleNippleShape()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleAreolaeSize() : startingBodyType.getMaleAreolaeSize()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleAreolaeShape() : startingBodyType.getMaleAreolaeShape()),
						(stage.isBreastFurry() ? (startingGender.isFeminine() ? startingBodyType.getFemaleNippleCountPerBreast() : startingBodyType.getMaleNippleCountPerBreast()) : 1),
						(startingGender.isFeminine() ? startingBodyType.getFemaleBreastCapacity() : startingBodyType.getMaleBreastCapacity()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleBreastDepth() : startingBodyType.getMaleBreastDepth()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleBreastElasticity() : startingBodyType.getMaleBreastElasticity()),
						(startingGender.isFeminine() ? startingBodyType.getFemaleBreastPlasticity() : startingBodyType.getMaleBreastPlasticity()),
						true),
				new Face((stage.isFaceFurry()?startingBodyType.getFaceType():FaceType.HUMAN),
						(startingGender.isFeminine() ? startingBodyType.getFemaleLipSize() : startingBodyType.getMaleLipSize())),
				new Eye(stage.isEyeFurry()?startingBodyType.getEyeType():EyeType.HUMAN),
				new Ear(stage.isEarFurry()?startingBodyType.getEarType():EarType.HUMAN),
				new Hair(stage.isHairFurry()?startingBodyType.getHairType():HairType.HUMAN,
						((scalyHairCheck && !Main.game.isScalyHairEnabled()) || (furryHairCheck && !Main.game.isFurryHairEnabled())
								?0
								:(startingGender.isFeminine()
								? startingBodyType.getFemaleHairLength()
								: startingBodyType.getMaleHairLength())),
						HairStyle.getRandomHairStyle(startingGender.isFeminine(), (startingGender.isFeminine() ? startingBodyType.getFemaleHairLength() : startingBodyType.getMaleHairLength())),
						stage),
				new Leg(stage.isLegFurry()?startingBodyType.getLegType():LegType.HUMAN, startingBodyType.getLegConfiguration()),
				new Torso(stage.isSkinFurry()?startingBodyType.getTorsoType():TorsoType.HUMAN),
				startingBodyType.getBodyMaterial(),
				startingBodyType.getGenitalArrangement(),
				(startingGender.isFeminine() ? startingBodyType.getFemaleHeight() : startingBodyType.getMaleHeight()),
				startingGender.getType()==PronounType.NEUTRAL?50:(startingGender.isFeminine() ? startingBodyType.getFemaleFemininity() : startingBodyType.getMaleFemininity()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleBodySize() : startingBodyType.getMaleBodySize()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleMuscle() : startingBodyType.getMaleMuscle()))
				.vagina(new Vagina(hasVagina?stage.isVaginaFurry()?startingBodyType.getVaginaType():VaginaType.HUMAN:VaginaType.NONE,
						LabiaSize.getRandomLabiaSize().getValue(),
						startingBodyType.getClitSize(),
						startingBodyType.getClitGirth(),
						startingBodyType.getVaginaWetness(),
						startingBodyType.getVaginaCapacity(),
						startingBodyType.getVaginaDepth(),
						startingBodyType.getVaginaElasticity(),
						startingBodyType.getVaginaPlasticity(),
						true))
				.penis(new Penis(hasPenis?stage.isPenisFurry()?startingBodyType.getPenisType():PenisType.HUMAN:PenisType.NONE,
						startingBodyType.getPenisSize(),
						true,
						startingBodyType.getPenisGirth(),
						startingBodyType.getTesticleSize(),
						startingBodyType.getCumProduction(),
						startingBodyType.getTesticleQuantity()))
				.horn(new Horn((stage.isHornFurry()?startingBodyType.getRandomHornType(false):HornType.NONE), (startingGender.isFeminine() ? startingBodyType.getFemaleHornLength() : startingBodyType.getMaleHornLength())))
				.antenna(new Antenna(stage.isAntennaFurry()?startingBodyType.getRandomrAntennaType(false):AntennaType.NONE, (startingGender.isFeminine() ? startingBodyType.getFemaleAntennaLength() : startingBodyType.getMaleAntennaLength())))
				.tail(new Tail(stage.isTailFurry()?startingBodyType.getRandomTailType(false):TailType.NONE))
				.tentacle(new Tentacle(stage.isTentacleFurry()?startingBodyType.getTentacleType():TentacleType.NONE))
				.wing(new Wing((stage.isWingFurry()?startingBodyType.getRandomWingType(false):WingType.NONE), (startingGender.isFeminine() ? startingBodyType.getFemaleWingSize() : startingBodyType.getMaleWingSize())))
				.breastCrotch(
						new BreastCrotch(
								startingGender.isFeminine()
										?startingBodyType.getBreastCrotchType()
										:BreastType.NONE,
								Util.randomItemFrom(startingBodyType.getBreastCrotchShapes()),
								startingBodyType.getBreastCrotchSize()+Main.getProperties().udderSizePreference,
								startingBodyType.getBreastCrotchLactationRate(),
								startingBodyType.getBreastCrotchCount(),
								startingBodyType.getBreastCrotchNippleSize(),
								startingBodyType.getBreastCrotchNippleShape(),
								startingBodyType.getBreastCrotchAreolaeSize(),
								startingBodyType.getBreastCrotchAreolaeShape(),
								startingBodyType.getNippleCountPerBreastCrotch(),
								startingBodyType.getBreastCrotchCapacity(),
								startingBodyType.getBreastCrotchDepth(),
								startingBodyType.getBreastCrotchElasticity(),
								startingBodyType.getBreastCrotchPlasticity(),
								true))
				.build();

		// Randomise skin colour if not greater by using skin preferences:
		if(body.getRaceStage()!=RaceStage.GREATER && body.getRaceStage()!=RaceStage.FERAL) {
			Colour skinColour = Util.randomItemFrom(BodyCoveringType.HUMAN.getNaturalColoursPrimary());
			if(Main.getProperties().skinColourPreferencesMap.values().stream().anyMatch(v->v>0)) {
				skinColour = Util.getRandomObjectFromWeightedMap(Main.getProperties().skinColourPreferencesMap);
			}
			body.setCovering(BodyCoveringType.HUMAN, Util.getRandomObjectFromWeightedMap(BodyCoveringType.HUMAN.getNaturalPatterns()), CoveringModifier.SMOOTH, skinColour, false, skinColour, false);
			body.updateCoverings(true, true, true, true);
		}

		if(linkedCharacter==null || !linkedCharacter.isUnique()) { // Unique characters should always have the default number of breast rows
			// Set breast rows based on preferences:
			if(Main.getProperties().multiBreasts==0 || Main.getProperties().multiBreasts==1) {
				body.getBreast().setRows(null, 1);

			} else if(Main.getProperties().multiBreasts==2) {
				if(body.getTorsoType()==TorsoType.HUMAN) {
					body.getBreast().setRows(null, 1);
				}
			}
		}

		// Set crotch boobs based on preferences:
		if(!body.isFeral()
				&& (Main.getProperties().getUddersLevel()==0
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && Main.getProperties().getUddersLevel()==1)
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && body.getRaceStage()!=RaceStage.GREATER))) {
			body.getBreastCrotch().setType(null, BreastType.NONE);
		}

		// Set external futa testicles based on preferences:
		if(body.getPenis().getType()!=PenisType.NONE
				&& body.getPenis().getType()!=PenisType.DILDO
				&& body.getVagina().getType()!=VaginaType.NONE
				&& body.getVagina().getType()!=VaginaType.ONAHOLE
				&& !Main.game.isFutanariTesticlesEnabled()) {
			body.getPenis().getTesticle().setInternal(null, true);
		}

		// Pubic hair:
		setBodyHair(body);

		if(species!=null) {
			if(stage!=RaceStage.HUMAN || isSlime) {
				species.getRace().applyRaceChanges(body);
				species.applySpeciesChanges(body);
			}
			if(isDoll) {
				Race.DOLL.applyRaceChanges(body);
				Subspecies.DOLL.applySpeciesChanges(body);
			}
		}
		if (materialSubspecies != null) {
			materialSubspecies.getRace().applyRaceChanges(body);
			materialSubspecies.applySpeciesChanges(body);
		}

		body.setSubspeciesOverride(null); // Set override to null so that it can be recalculated based on the final body type.
		body.calculateRace(linkedCharacter);

		return body;
	}

	/**
	 * If you are wanting to change a newly-spawned NPC's body, then <b>you should consider using GameCharacter.setBody() instead</b>, as that method can also apply personality changes.
	 */
	public Body reassignBody(GameCharacter linkedCharacter, Body body, Gender startingGender, AbstractSubspecies species, RaceStage stage, boolean removeDemonOverride) {
		if(removeDemonOverride) {
			body.setSubspeciesOverride(null);
		}

		AbstractRacialBody startingBodyType = RacialBody.valueOfRace(species.getRace());

		boolean hasVagina = startingGender.getGenderName().isHasVagina();
		boolean hasPenis = startingGender.getGenderName().isHasPenis();
		boolean hasBreasts = startingGender.getGenderName().isHasBreasts();
		boolean[] virginities = null;

		// Save virginities to be restored after body reset:
		if(linkedCharacter!=null) {
			virginities = new boolean[] {
					linkedCharacter.isAnalVirgin(),
					linkedCharacter.isAssVirgin(),
					linkedCharacter.isFaceVirgin(),
					linkedCharacter.isNippleCrotchVirgin(),
					linkedCharacter.isNippleVirgin(),
					linkedCharacter.isPenisVirgin(),
					linkedCharacter.isSpinneretVirgin(),
					linkedCharacter.isUrethraVirgin(),
					linkedCharacter.isVaginaUrethraVirgin(),
					linkedCharacter.isVaginaVirgin(),
					linkedCharacter.hasHymen()
			};
		}

		body.setArm(new Arm((stage.isArmFurry()?startingBodyType.getArmType():ArmType.HUMAN), startingBodyType.getArmRows()));

		body.setAss(new Ass(stage.isAssFurry()?startingBodyType.getAssType():AssType.HUMAN,
				(startingGender.isFeminine() ? startingBodyType.getFemaleAssSize() : startingBodyType.getMaleAssSize()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleHipSize() : startingBodyType.getMaleHipSize()),
				startingBodyType.getAnusWetness(),
				startingBodyType.getAnusCapacity(),
				startingBodyType.getAnusDepth(),
				startingBodyType.getAnusElasticity(),
				startingBodyType.getAnusPlasticity(),
				true));

		body.setBreast(new Breast(stage.isBreastFurry()?startingBodyType.getBreastType():BreastType.HUMAN,
				Util.randomItemFrom(startingBodyType.getBreastShapes()),
				(hasBreasts
						? Math.max(CupSize.getMinimumCupSizeForBreasts().getMeasurement(), startingBodyType.getBreastSize()+Main.getProperties().breastSizePreference)
						: startingBodyType.getNoBreastSize()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleLactationRate() : startingBodyType.getMaleLactationRate()),
				(startingGender.isFeminine() ? startingBodyType.getBreastCountFemale() : startingBodyType.getBreastCountMale()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleNippleSize() : startingBodyType.getMaleNippleSize()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleNippleShape() : startingBodyType.getMaleNippleShape()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleAreolaeSize() : startingBodyType.getMaleAreolaeSize()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleAreolaeShape() : startingBodyType.getMaleAreolaeShape()),
				(stage.isBreastFurry() ? (startingGender.isFeminine() ? startingBodyType.getFemaleNippleCountPerBreast() : startingBodyType.getMaleNippleCountPerBreast()) : 1),
				(startingGender.isFeminine() ? startingBodyType.getFemaleBreastCapacity() : startingBodyType.getMaleBreastCapacity()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleBreastDepth() : startingBodyType.getMaleBreastDepth()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleBreastElasticity() : startingBodyType.getMaleBreastElasticity()),
				(startingGender.isFeminine() ? startingBodyType.getFemaleBreastPlasticity() : startingBodyType.getMaleBreastPlasticity()),
				true));

		body.setBreastCrotch(
				new BreastCrotch(
						startingGender.isFeminine()
								?startingBodyType.getBreastCrotchType()
								:BreastType.NONE,
						Util.randomItemFrom(startingBodyType.getBreastCrotchShapes()),
						startingBodyType.getBreastCrotchSize()+Main.getProperties().udderSizePreference,
						startingBodyType.getBreastCrotchLactationRate(),
						startingBodyType.getBreastCrotchCount(),
						startingBodyType.getBreastCrotchNippleSize(),
						startingBodyType.getBreastCrotchNippleShape(),
						startingBodyType.getBreastCrotchAreolaeSize(),
						startingBodyType.getBreastCrotchAreolaeShape(),
						startingBodyType.getNippleCountPerBreastCrotch(),
						startingBodyType.getBreastCrotchCapacity(),
						startingBodyType.getBreastCrotchDepth(),
						startingBodyType.getBreastCrotchElasticity(),
						startingBodyType.getBreastCrotchPlasticity(),
						true));

		body.setFace(new Face((stage.isFaceFurry()?startingBodyType.getFaceType():FaceType.HUMAN),
				(startingGender.isFeminine() ? startingBodyType.getFemaleLipSize() : startingBodyType.getMaleLipSize())));

		body.setEye(new Eye(stage.isEyeFurry()?startingBodyType.getEyeType():EyeType.HUMAN));

		body.setEar(new Ear(stage.isEarFurry()?startingBodyType.getEarType():EarType.HUMAN));

		boolean furryHairCheck = stage.isFaceFurry() && startingBodyType.getFaceType().getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_FURRY) && !startingBodyType.getHairType().getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);
		boolean scalyHairCheck = stage.isFaceFurry() && startingBodyType.getFaceType().getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_SCALY) && !startingBodyType.getHairType().getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);
		body.setHair(new Hair(stage.isHairFurry()?startingBodyType.getHairType():HairType.HUMAN,
				((scalyHairCheck && !Main.game.isScalyHairEnabled()) || (furryHairCheck && !Main.game.isFurryHairEnabled())
						?0
						:(startingGender.isFeminine()
						? startingBodyType.getFemaleHairLength()
						: startingBodyType.getMaleHairLength())),
				HairStyle.getRandomHairStyle(startingGender.isFeminine(), (startingGender.isFeminine() ? startingBodyType.getFemaleHairLength() : startingBodyType.getMaleHairLength())),
				stage));

		body.setLeg(new Leg(stage.isLegFurry()?startingBodyType.getLegType():LegType.HUMAN, startingBodyType.getLegConfiguration()));

		body.setTorso(new Torso(stage.isSkinFurry()?startingBodyType.getTorsoType():TorsoType.HUMAN));

		body.setBodyMaterial(startingBodyType.getBodyMaterial());

		body.setGenitalArrangement(startingBodyType.getGenitalArrangement());

		body.setHeight(startingGender.isFeminine()
				? startingBodyType.getFemaleHeight()
				: startingBodyType.getMaleHeight());

		body.setFemininity(startingGender.getType()==PronounType.NEUTRAL?50:(startingGender.isFeminine() ? startingBodyType.getFemaleFemininity() : startingBodyType.getMaleFemininity()));

		body.setBodySize(startingGender.isFeminine() ? startingBodyType.getFemaleBodySize() : startingBodyType.getMaleBodySize());

		body.setMuscle(startingGender.isFeminine() ? startingBodyType.getFemaleMuscle() : startingBodyType.getMaleMuscle());

		body.setVagina(hasVagina
				? new Vagina(stage.isVaginaFurry()?startingBodyType.getVaginaType():VaginaType.HUMAN,
				LabiaSize.getRandomLabiaSize().getValue(),
				startingBodyType.getClitSize(),
				startingBodyType.getClitGirth(),
				startingBodyType.getVaginaWetness(),
				startingBodyType.getVaginaCapacity(),
				startingBodyType.getVaginaDepth(),
				startingBodyType.getVaginaElasticity(),
				startingBodyType.getVaginaPlasticity(),
				true)
				: new Vagina(VaginaType.NONE, 0, 0, 0, 0, 0, 2, 3, 3, true));

		body.setPenis(hasPenis
				? new Penis(stage.isPenisFurry()?startingBodyType.getPenisType():PenisType.HUMAN,
				startingBodyType.getPenisSize(),
				true,
				startingBodyType.getPenisGirth(),
				startingBodyType.getTesticleSize(),
				startingBodyType.getCumProduction(),
				startingBodyType.getTesticleQuantity())
				: new Penis(PenisType.NONE, 0, false, 0, 0, 0, 2));

		body.setHorn(new Horn((stage.isHornFurry()?startingBodyType.getRandomHornType(false):HornType.NONE), (startingGender.isFeminine() ? startingBodyType.getFemaleHornLength() : startingBodyType.getMaleHornLength())));

		body.setAntenna(new Antenna(stage.isAntennaFurry()?startingBodyType.getRandomrAntennaType(false):AntennaType.NONE, (startingGender.isFeminine() ? startingBodyType.getFemaleAntennaLength() : startingBodyType.getMaleAntennaLength())));

		body.setTail(new Tail(stage.isTailFurry()?startingBodyType.getRandomTailType(false):TailType.NONE));

		body.setTentacle(new Tentacle(stage.isTentacleFurry()?startingBodyType.getTentacleType():TentacleType.NONE));

		body.setWing(new Wing((stage.isWingFurry()?startingBodyType.getRandomWingType(false):WingType.NONE), (startingGender.isFeminine() ? startingBodyType.getFemaleWingSize() : startingBodyType.getMaleWingSize())));

		if(body.getPenis().getType()!=PenisType.NONE
				&& body.getPenis().getType()!=PenisType.DILDO
				&& body.getVagina().getType()!=VaginaType.NONE
				&& body.getVagina().getType()!=VaginaType.ONAHOLE
				&& !Main.game.isFutanariTesticlesEnabled()) {
			body.getPenis().getTesticle().setInternal(null, true);
		}

		// Pubic hair:
		setBodyHair(body);

		if(species!=null && stage!=RaceStage.HUMAN) {
			species.getRace().applyRaceChanges(body);
			species.applySpeciesChanges(body);
		}
		body.calculateRace(linkedCharacter);
		// To add or remove youko perks
		body.getTail().setTailCount(linkedCharacter, body.getTail().getTailCount(), true);

		if(!body.isFeral()
				&& (Main.getProperties().getUddersLevel()==0
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && Main.getProperties().getUddersLevel()==1)
				|| (body.getLeg().getLegConfiguration()==LegConfiguration.BIPEDAL && body.getRaceStage()!=RaceStage.GREATER))) {
			body.getBreastCrotch().setType(null, BreastType.NONE);
		}

		if(linkedCharacter!=null) {
			linkedCharacter.postTransformationCalculation();
		}

		if(linkedCharacter!=null) {
			linkedCharacter.setAnalVirgin(virginities[0]);
			linkedCharacter.setAssVirgin(virginities[1]);
			linkedCharacter.setFaceVirgin(virginities[2]);
			linkedCharacter.setNippleCrotchVirgin(virginities[3]);
			linkedCharacter.setNippleVirgin(virginities[4]);
			linkedCharacter.setPenisVirgin(virginities[5]);
			linkedCharacter.setSpinneretVirgin(virginities[6]);
			linkedCharacter.setUrethraVirgin(virginities[7]);
			linkedCharacter.setVaginaUrethraVirgin(virginities[8]);
			linkedCharacter.setVaginaVirgin(virginities[9]);
			linkedCharacter.setHymen(virginities[10]);
		}

		return body;
	}

	public void applyTaurConversion(GameCharacter character) {
		int taurLevel = Main.getProperties().taurFurryLevel;
		if(character.getRace()==Race.DEMON && character.getSubspeciesOverride()!=Subspecies.HALF_DEMON) {
			taurLevel = 0; // Demons and imps should always be untouched
		}
		RaceStage raceStage = null;

		switch(taurLevel) {
			case 0: // Untouched
				break;
			case 1:
				raceStage = RaceStage.HUMAN;
				break;
			case 2:
				raceStage = RaceStage.PARTIAL;
				break;
			case 3:
				raceStage = Util.randomItemFrom(Util.newArrayListOfValues(RaceStage.PARTIAL, RaceStage.LESSER));
				break;
			case 4:
				raceStage = Util.randomItemFrom(Util.newArrayListOfValues(RaceStage.PARTIAL, RaceStage.LESSER, RaceStage.GREATER));
				break;
			case 5:
				raceStage = RaceStage.GREATER;
				break;
		}

		character.setLegConfiguration(LegConfiguration.QUADRUPEDAL, true);

		if(raceStage==null) {
			return;
		}

		boolean halfDemon = character.getSubspeciesOverride()==Subspecies.HALF_DEMON;
		AbstractHairType furryHairType = Util.randomItemFrom(HairType.getHairTypes(character.getLegRace()));

		switch(raceStage) {
			case FERAL:
				System.err.println("Warning: applyTaurConversion() was called on the feral character '"+character.getName()+"'!");
				break;
			case HUMAN:
				if(!halfDemon) {
					character.setBreastType(BreastType.HUMAN);
					character.setEarType(EarType.HUMAN);
					character.setEyeType(EyeType.HUMAN);
					character.setHairType(HairType.HUMAN);
					character.setHornType(HornType.NONE);
				}
				character.setAntennaType(AntennaType.NONE);
				character.setArmType(ArmType.HUMAN);
				character.setFaceType(FaceType.HUMAN);
				character.setTorsoType(TorsoType.HUMAN);
				// Reset hair length:
				character.setHairLength((character.isFeminine()
						? RacialBody.valueOfRace(character.getRace()).getFemaleHairLength()
						: RacialBody.valueOfRace(character.getRace()).getMaleHairLength()));
				break;
			case PARTIAL:
			case PARTIAL_FULL:
				if(!halfDemon) {
					character.setBreastType(BreastType.HUMAN);
					character.setEyeType(Util.randomItemFrom(EyeType.getEyeTypes(character.getLegRace())));
					character.setHornType(Util.randomItemFrom(character.getLegRace().getRacialBody().getHornTypes(false)));
				}
				character.setAntennaType(Util.randomItemFrom(AntennaType.getAntennaTypes(character.getLegRace())));
				character.setArmType(ArmType.HUMAN);
				character.setEarType(Util.randomItemFrom(EarType.getEarTypes(character.getLegRace())));
				character.setFaceType(FaceType.HUMAN);
				character.setTorsoType(TorsoType.HUMAN);
				// Reset hair length:
				character.setHairLength((character.isFeminine()
						? RacialBody.valueOfRace(character.getRace()).getFemaleHairLength()
						: RacialBody.valueOfRace(character.getRace()).getMaleHairLength()));
				break;
			case LESSER:
				if(!halfDemon) {
					character.setBreastType(Util.randomItemFrom(BreastType.getBreastTypes(character.getLegRace())));
					character.setEyeType(Util.randomItemFrom(EyeType.getEyeTypes(character.getLegRace())));
					character.setHornType(Util.randomItemFrom(character.getLegRace().getRacialBody().getHornTypes(false)));
				}
				character.setAntennaType(Util.randomItemFrom(AntennaType.getAntennaTypes(character.getLegRace())));
				character.setArmType(Util.randomItemFrom(ArmType.getArmTypes(character.getLegRace())));
				character.setEarType(Util.randomItemFrom(EarType.getEarTypes(character.getLegRace())));
				character.setFaceType(FaceType.HUMAN);
				character.setHairType(furryHairType);
				character.setTorsoType(TorsoType.HUMAN);
				// Reset hair length:
				character.setHairLength((character.isFeminine()
						? RacialBody.valueOfRace(character.getRace()).getFemaleHairLength()
						: RacialBody.valueOfRace(character.getRace()).getMaleHairLength()));
				break;
			case GREATER:
				if(!halfDemon) {
					character.setBreastType(Util.randomItemFrom(BreastType.getBreastTypes(character.getLegRace())));
					character.setEyeType(Util.randomItemFrom(EyeType.getEyeTypes(character.getLegRace())));
					character.setHornType(Util.randomItemFrom(character.getLegRace().getRacialBody().getHornTypes(false)));
				}

				AbstractFaceType faceType = Util.randomItemFrom(FaceType.getFaceTypes(character.getLegRace()));
				boolean furryHairCheck = faceType.getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_FURRY) && !furryHairType.getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);
				boolean scalyHairCheck = faceType.getTags().contains(BodyPartTag.FACE_NATURAL_BALDNESS_SCALY) && !furryHairType.getTags().contains(BodyPartTag.HAIR_IGNORE_PLAYER_SETTINGS);

				character.setAntennaType(Util.randomItemFrom(AntennaType.getAntennaTypes(character.getLegRace())));
				character.setArmType(Util.randomItemFrom(ArmType.getArmTypes(character.getLegRace())));
				character.setEarType(Util.randomItemFrom(EarType.getEarTypes(character.getLegRace())));
				character.setFaceType(faceType);
				character.setHairType(Util.randomItemFrom(HairType.getHairTypes(character.getLegRace())));
				character.setTorsoType(Util.randomItemFrom(TorsoType.getTorsoTypes(character.getLegRace())));
				// Reset hair length:
				if((scalyHairCheck && !Main.game.isScalyHairEnabled()) || (furryHairCheck && !Main.game.isFurryHairEnabled())) {
					character.setHairLength(0);
				} else {
					character.setHairLength((character.isFeminine()
							? RacialBody.valueOfRace(character.getRace()).getFemaleHairLength()
							: RacialBody.valueOfRace(character.getRace()).getMaleHairLength()));
				}
				break;
		}
	}

	private static void setBodyHair(Body body) {
		int slobLevel = (int) (Util.random.nextInt(101)*body.getRace().getDisposition().getBodyHairModifier());

		boolean facialHair = body.getFace().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		boolean underarmHair = body.getArm().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		boolean assHair = body.getAss().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		boolean pubicHair;
		if(body.getPenis().getType()!=PenisType.NONE && body.getPenis().getType()!=PenisType.DILDO) {
			pubicHair = body.getPenis().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		} else if(body.getVagina().getType()!=VaginaType.NONE && body.getVagina().getType()!=VaginaType.ONAHOLE) {
			pubicHair = body.getVagina().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		} else {
			pubicHair = body.getTorso().getType().getRace().getRacialClass().isAnthroHair() || Main.game.isScalyHairEnabled();
		}

		BodyHair facialHairLevel = BodyHair.ZERO_NONE;
		BodyHair underarmHairLevel = BodyHair.ZERO_NONE;
		BodyHair assHairLevel = BodyHair.ZERO_NONE;
		BodyHair pubicHairLevel = BodyHair.ZERO_NONE;

		if(body.isFeminine()) {
			facialHairLevel = BodyHair.ZERO_NONE;
			if(slobLevel>=95) {
				underarmHairLevel = BodyHair.SIX_BUSHY;
				pubicHairLevel = BodyHair.SEVEN_WILD;
				assHairLevel = BodyHair.FIVE_UNKEMPT;

			} else if(slobLevel>=80) {
				underarmHairLevel = BodyHair.FOUR_NATURAL;
				pubicHairLevel = BodyHair.SIX_BUSHY;
				assHairLevel = BodyHair.FOUR_NATURAL;

			} else if(slobLevel>=60) {
				underarmHairLevel = BodyHair.ZERO_NONE;
				pubicHairLevel = BodyHair.FOUR_NATURAL;
				assHairLevel = BodyHair.TWO_MANICURED;

			} else if(slobLevel>=40) {
				underarmHairLevel = BodyHair.ZERO_NONE;
				pubicHairLevel = BodyHair.TWO_MANICURED;
				assHairLevel = BodyHair.ZERO_NONE;

			} else {
				underarmHairLevel = BodyHair.ZERO_NONE;
				pubicHairLevel = BodyHair.ZERO_NONE;
				assHairLevel = BodyHair.ZERO_NONE;
			}

		} else {
			if(slobLevel>=95) {
				if(Math.random()>0.5) {
					facialHairLevel = BodyHair.SEVEN_WILD;
				} else {
					facialHairLevel = BodyHair.ZERO_NONE;
				}
				underarmHairLevel = BodyHair.SIX_BUSHY;
				pubicHairLevel = BodyHair.SEVEN_WILD;
				assHairLevel = BodyHair.FIVE_UNKEMPT;

			} else if(slobLevel>=80) {
				if(Math.random()>0.6) {
					facialHairLevel = BodyHair.SIX_BUSHY;
				} else {
					facialHairLevel = BodyHair.ZERO_NONE;
				}
				underarmHairLevel = BodyHair.FOUR_NATURAL;
				pubicHairLevel = BodyHair.SIX_BUSHY;
				assHairLevel = BodyHair.FOUR_NATURAL;

			} else if(slobLevel>=60) {
				if(Math.random()>0.7) {
					facialHairLevel = BodyHair.FOUR_NATURAL;
				} else {
					facialHairLevel = BodyHair.ZERO_NONE;
				}
				underarmHairLevel = BodyHair.FOUR_NATURAL;
				pubicHairLevel = BodyHair.FOUR_NATURAL;
				assHairLevel = BodyHair.TWO_MANICURED;

			} else if(slobLevel>=20) {
				if(Math.random()>0.8) {
					facialHairLevel = BodyHair.ONE_STUBBLE;
				} else {
					facialHairLevel = BodyHair.ZERO_NONE;
				}
				underarmHairLevel = BodyHair.FOUR_NATURAL;
				pubicHairLevel = BodyHair.THREE_TRIMMED;
				assHairLevel = BodyHair.ZERO_NONE;

			} else {
				underarmHairLevel = BodyHair.ZERO_NONE;
				pubicHairLevel = BodyHair.ZERO_NONE;
				assHairLevel = BodyHair.ZERO_NONE;
			}
		}

		if(!facialHair) {
			facialHairLevel = BodyHair.ZERO_NONE;
		}
		if(!underarmHair) {
			underarmHairLevel = BodyHair.ZERO_NONE;
		}
		if(!pubicHair) {
			pubicHairLevel = BodyHair.ZERO_NONE;
		}
		if(!assHair) {
			assHairLevel = BodyHair.ZERO_NONE;
		}

		body.getFace().setFacialHair(null, facialHairLevel);
		body.getArm().setUnderarmHair(null, underarmHairLevel);
		body.setPubicHair(pubicHairLevel);
		body.getAss().getAnus().setAssHair(null, assHairLevel);
	}



	public String setGenericName(GameCharacter character, List<String> exclusiveAdjectives) {
		return setGenericName(character, null, exclusiveAdjectives);
	}
	/**
	 * Generates and sets a generic name for this character, based on their personality.
	 * @param character The character to set a generic name for.
	 * @param exclusiveAdjectives A list of adjectives to exclude from the random assignment.
	 * @return The adjective that was chosen to describe the character.
	 */
	public String setGenericName(GameCharacter character, String baseName, List<String> exclusiveAdjectives) {

		List<String> characterAdjectives = new ArrayList<>();
		for(PersonalityTrait trait : character.getPersonalityTraits()) {
			switch(trait) {
				case BRAVE:
					characterAdjectives.add("daring");
					characterAdjectives.add("fearless");
					characterAdjectives.add("brash");
					characterAdjectives.add("cocky");
					break;
				case COWARDLY:
					characterAdjectives.add("suspicious");
					characterAdjectives.add("cowardly");
					break;
				case CONFIDENT:
					characterAdjectives.add("excitable");
					characterAdjectives.add("energetic");
					characterAdjectives.add("playful");
					break;
				case KIND:
					break;
				case LEWD:
					characterAdjectives.add("lewd");
					characterAdjectives.add("crass");
					characterAdjectives.add("vulgar");
					characterAdjectives.add("licentious");
					break;
				case PRUDE:
					characterAdjectives.add("prude");
					break;
				case SELFISH:
				case CYNICAL:
					characterAdjectives.add("rude");
					break;
				case SHY:
					characterAdjectives.add("nervous");
					characterAdjectives.add("shy");
					break;
//				case BIMBO:
//				case BRO:
				case LISP:
				case SLOVENLY:
					break;
				case STUTTER:
					characterAdjectives.add("stuttering");
					break;
				case MUTE:
					break;
				case INNOCENT:
					break;
				case NAIVE:
					break;
			}
		}

		if(exclusiveAdjectives!=null) {
			characterAdjectives.removeAll(exclusiveAdjectives);
		}
		if(characterAdjectives.isEmpty()) {
			characterAdjectives = Util.newArrayListOfValues("cheeky", "excitable", "energetic", "cunning", "rude", "cocky", "smug");
			if(exclusiveAdjectives!=null) {
				characterAdjectives.removeAll(exclusiveAdjectives);
			}
		}

		String adjective = Util.randomItemFrom(characterAdjectives);

		if(baseName==null || baseName.isEmpty()) {
			character.setGenericName(adjective+" "+character.getSubspecies().getName(character.getBody()));
		} else {
			character.setGenericName(adjective+" "+baseName);
		}

		return adjective;
	}

	public void randomiseBody(GameCharacter character, boolean randomiseAge) {

		if(randomiseAge) {
			int dayOfMonth = character.getDayOfBirth();
			if(character.getBirthMonth() == Month.FEBRUARY) { // Don't set a character's birthday to a leap day as otherwise it ends up causing messy issues.
				dayOfMonth = Math.min(dayOfMonth, 28);
			}
			if(character.getSubspeciesOverride()!=null && character.getSubspeciesOverride().isDoesNotAge()) {
				character.setBirthday(LocalDateTime.of(Main.game.getDateNow().getYear()-(Util.random.nextInt(101)-GameCharacter.MINIMUM_AGE), character.getBirthMonth(), dayOfMonth, 12, 0));
			} else {
				character.setBirthday(LocalDateTime.of(Main.game.getDateNow().getYear()-(AgeCategory.getAgeFromPreferences(character.getGender())-GameCharacter.MINIMUM_AGE), character.getBirthMonth(), dayOfMonth, 12, 0));
			}
			character.setConceptionDate(character.getBirthday().minusDays(15+Util.random.nextInt(30)));

			if(character.getRace()==Race.HARPY) {
				character.setAgeAppearanceDifferenceToAppearAsAge(Math.min(character.getAgeValue(), 18+Util.random.nextInt(9)));
			}
			if(character.getSubspeciesOverride()!=null && character.getSubspeciesOverride().isDoesNotAge()) {
				character.setAgeAppearanceAbsolute(Math.min(character.getAgeValue(), 18+Util.random.nextInt(19))); // Range of real age to 36
				//System.out.println("Override: "+character.getAgeAppearanceAbsolute()+", "+character.getAgeValue());
			}
		}

		// Piercings (in order of probability that they'll have them, based on some random website that orders popularity):
		// All piercings are reliant on having ear piercings first:
		if (Math.random() >= (character.isFeminine()?0.1f:0.9f) || character.hasFetish(Fetish.FETISH_MASOCHIST)) {
			character.setPiercedEar(true);

			if (Math.random() <= 0.33f || character.hasFetish(Fetish.FETISH_MASOCHIST)) {
				character.setPiercedNavel(true);
			}
			if (Math.random() <= 0.19f || character.hasFetish(Fetish.FETISH_MASOCHIST)) {
				character.setPiercedNose(true);
			}
			if (Math.random() <= 0.1f || character.hasFetish(Fetish.FETISH_MASOCHIST)) {
				character.setPiercedTongue(true);
			}
			if (Math.random() <= 0.1f || character.hasFetish(Fetish.FETISH_MASOCHIST)) {
				character.setPiercedNipples(true);
			}
			if (Math.random() <= 0.1f || character.hasFetish(Fetish.FETISH_MASOCHIST)) { // It said lip piercings are only in 4% of the pierced population, but that seems too low for the game.
				character.setPiercedLip(true);
			}
			// Genitals:
			if (character.hasPenis() && (Math.random() <= 0.05f || character.hasFetish(Fetish.FETISH_MASOCHIST))) {
				character.setPiercedPenis(true);
			}
			if (character.hasVagina() && (Math.random() <= 0.05f || character.hasFetish(Fetish.FETISH_MASOCHIST))) {
				character.setPiercedVagina(true);
			}
		}

		// Body parts:

		// Randomise skin colour if not greater:
		if(character.getRaceStage()!=RaceStage.GREATER && character.getRaceStage()!=RaceStage.FERAL) {
//			character.setSkinCovering(BodyCoveringType.HUMAN, Util.randomItemFrom(BodyCoveringType.HUMAN.getNaturalColoursPrimary()), true);
			Colour skinColour = Util.randomItemFrom(BodyCoveringType.HUMAN.getNaturalColoursPrimary());
			if(Main.getProperties().skinColourPreferencesMap.values().stream().anyMatch(v->v>0)) {
				skinColour = Util.getRandomObjectFromWeightedMap(Main.getProperties().skinColourPreferencesMap);
			}
			character.setSkinCovering(BodyCoveringType.HUMAN, skinColour, true);
		}

		// Ass:
		if(character.hasFetish(Fetish.FETISH_ANAL_RECEIVING) || character.getHistory()==Occupation.NPC_PROSTITUTE) {
			character.setAssVirgin(false);
			character.setAssCapacity(character.getAssRawCapacityValue()*1.2f, true);

		} else {
			character.setAssVirgin(true);
		}
		int assHipVariation = - 1 + Util.random.nextInt(3); // -1 to 1
		character.setAssSize(character.getAssSize().getValue() + assHipVariation);
		character.setHipSize(character.getHipSize().getValue() - assHipVariation);

		// Body (height):
		int height = character.getHeightValue() - 19 + Util.random.nextInt(20) + Util.random.nextInt(20);

		// Keep height within category if short stature:
		if(character.getHeight()==Height.NEGATIVE_THREE_MINIMUM
				|| character.getHeight()==Height.NEGATIVE_TWO_MINIMUM
				|| character.getHeight()==Height.NEGATIVE_ONE_TINY
				|| character.getHeight()==Height.ZERO_TINY) {
			character.setHeight(Math.min(character.getHeight().getMaximumValue()-1, Math.max(character.getHeight().getMinimumValue(), height)));

		} else {
			character.setHeight(Math.max(Height.getShortStatureCutOff(), height));
		}

		// Body (femininity):
		int femininityVariation = -20 + Util.random.nextInt(21) + Util.random.nextInt(21);

		switch(character.getFemininity()) { // Do not move out of masculine/feminine/androgynous zones:
			case ANDROGYNOUS:
				character.setFemininity(Math.max(Femininity.ANDROGYNOUS.getMinimumFemininity(), Math.min(Femininity.ANDROGYNOUS.getMaximumFemininity(), character.getFemininityValue()+femininityVariation)));
				break;
			case FEMININE:
			case FEMININE_STRONG:
				character.setFemininity(Math.max(Femininity.FEMININE.getMinimumFemininity(), character.getFemininityValue()+femininityVariation));
				break;
			case MASCULINE:
			case MASCULINE_STRONG:
				character.setFemininity(Math.min(Femininity.MASCULINE.getMaximumFemininity(), character.getFemininityValue()+femininityVariation));
				break;
		}

		// Body (muscle and size):
		int muscleVariation = -10 + Util.random.nextInt(11) + Util.random.nextInt(11); // -10 to 10
		character.incrementMuscle(muscleVariation);

		int bodySizeVariation = -10 + Util.random.nextInt(11) + Util.random.nextInt(11); // -10 to 10
		character.incrementBodySize(bodySizeVariation);

		//Breasts:
		if(character.hasBreasts()) {
			character.setBreastSize(Math.max(CupSize.AA.getMeasurement(), character.getBreastSize().getMeasurement() -2 + Util.random.nextInt(5))); // Random size between -2 and +2 of base value.
			if(Math.random()<=0.015f || character.hasFetish(Fetish.FETISH_LACTATION_SELF)) {
				character.setBreastMilkStorage((int)((character.getBreastSize().getMeasurement() * 5)*(1+(Math.random()*2))));
				if(Math.random()<=0.025f) {
					character.addMilkModifier(FluidModifier.ADDICTIVE);
				}
				if(Math.random()<=0.025f) {
					character.addMilkModifier(FluidModifier.HALLUCINOGENIC);
				}
			}
			if(character.getNippleShape()==NippleShape.NORMAL
					&& Math.random()<0.025) {
				character.setNippleShape(NippleShape.INVERTED);
			}
		}
		if(character.isFeminine()) {
			int nippleVariation = Util.random.nextInt(2); // 0-1
			character.incrementNippleSize(nippleVariation);
			character.incrementAreolaeSize(nippleVariation);
		}

		//BreastsCrotch:
		if(character.hasBreastsCrotch()) {
			character.setBreastCrotchSize(Math.max(CupSize.FLAT.getMeasurement(), character.getBreastCrotchSize().getMeasurement() -2 + Util.random.nextInt(5)));// Random size between -2 and +2 of base value.
			// If bipedal, make sure it's smaller than the smallest upper cup size:
			if(character.getLegConfiguration()==LegConfiguration.BIPEDAL) {
				character.setBreastCrotchSize(Math.min(character.getBreastCrotchRawSizeValue(), character.getBreastRawSizeValue()-(character.getBreastRows())));
			}

			if(Math.random()<=0.015f || character.hasFetish(Fetish.FETISH_LACTATION_SELF)) {
				character.setBreastCrotchMilkStorage((int)((character.getBreastCrotchSize().getMeasurement() * 5)*(1+(Math.random()*2))));
				if(Math.random()<=0.025f) {
					character.addMilkCrotchModifier(FluidModifier.ADDICTIVE);
				}
				if(Math.random()<=0.025f) {
					character.addMilkCrotchModifier(FluidModifier.HALLUCINOGENIC);
				}
			}
			if(character.getNippleCrotchShape()==NippleShape.INVERTED
					&& character.getBreastCrotchShape()!=BreastShape.UDDERS) {
				character.setNippleCrotchShape(NippleShape.INVERTED);
			}
			if(character.isFeminine()) {
				int nippleCrotchVariation = Util.random.nextInt(2); // 0-1
				character.incrementNippleCrotchSize(nippleCrotchVariation);
				character.incrementAreolaeCrotchSize(nippleCrotchVariation);
			}
		}

		// Eye:
		if (Math.random()<0.35f && character.getBodyMaterial() == BodyMaterial.FLESH) {
			randomiseEyeColour(character);
		}
		if (Math.random()<0.15f && character.getBodyMaterial() == BodyMaterial.FLESH) {
			randomiseEyeColour(character);
		}
		// Face:
		if(character.hasFetish(Fetish.FETISH_ORAL_GIVING) || character.getHistory()==Occupation.NPC_PROSTITUTE) {
			character.setFaceCapacity(Capacity.FIVE_ROOMY.getMedianValue(), true);
			character.setFaceVirgin(false);

		} else {
			if(Math.random()<0.85f) {
				character.setFaceVirgin(false);
			} else {
				character.setFaceVirgin(true);
			}
		}
		if(character.isFeminine()) {
			int lipVariation = Util.random.nextInt(2); // 0-1
			character.incrementLipSize(lipVariation);
		}

		// Hair:
		if(Math.random()<=0.1f && !character.getCovering(character.getHairCovering()).getType().getDyePatterns().isEmpty()) { // 10% chance to have a non-natural hair colour:
			Covering currentCovering = character.getCovering(character.getHairCovering());
			character.setHairCovering(new Covering(
							currentCovering.getType(),
							Util.getRandomObjectFromWeightedMap(currentCovering.getType().getDyePatterns()),
							currentCovering.getType().getAllPrimaryColours().isEmpty()
									?currentCovering.getPrimaryColour()
									:Util.randomItemFrom(currentCovering.getType().getAllPrimaryColours()),
							Math.random()<=0.05f,
							currentCovering.getType().getAllSecondaryColours().isEmpty()
									?currentCovering.getSecondaryColour()
									:Util.randomItemFrom(currentCovering.getType().getAllSecondaryColours()),
							Math.random()<=0.05f),
					true);
		}
		if(character.getHairRawLengthValue()!=0) {
			character.setHairLength(character.getHairLength().getMinimumValue() + Util.random.nextInt(character.getHairLength().getMaximumValue() - character.getHairLength().getMinimumValue()) +1);
		}

		// Penis:
		if(character.hasPenis() || character.getSubspeciesOverrideRace()==Race.DEMON) {
			if(Math.random()<0.95f
					|| character.getHistory()==Occupation.NPC_PROSTITUTE
					|| character.hasFetish(Fetish.FETISH_CUM_STUD)
					|| character.hasFetish(Fetish.FETISH_VAGINAL_GIVING)
					|| character.hasFetish(Fetish.FETISH_ANAL_GIVING)) {
				character.setPenisVirgin(false);
			} else {
				character.setPenisVirgin(true);
			}
			if((character.getGender()==Gender.F_P_TRAP || character.getGender()==Gender.N_P_TRAP)) {
				float sizeAlteration = (100+Main.getProperties().trapPenisSizePreference)/100f;

				character.setPenisSize(character.getPenisSize().getMinimumValue() + Util.random.nextInt(character.getPenisSize().getMaximumValue() - character.getPenisSize().getMinimumValue()) +1);
				character.setPenisSize(Math.max(1, (int)(character.getPenisRawSizeValue()*sizeAlteration)));

//				character.setPenisGirth(Math.round(character.getPenisGirth().getValue()*sizeAlteration));

				character.setTesticleSize(Math.round(character.getTesticleSize().getValue()*sizeAlteration));

				character.setPenisCumStorage(Math.max(1, (int)(character.getCurrentPenisRawCumStorageValue()*sizeAlteration)));

			} else {
				character.setPenisSize(character.getPenisSize().getMinimumValue() + Util.random.nextInt(character.getPenisSize().getMaximumValue() - character.getPenisSize().getMinimumValue()) +1);

				int testicleSize = character.getTesticleSize().getValue();
				testicleSize += Util.random.nextInt(3) - 1;
				character.setTesticleSize(testicleSize);

				int cumProduction = character.getPenisRawCumStorageValue();
				if(cumProduction>0) {
					cumProduction += Util.random.nextInt(cumProduction) - (cumProduction/2);
					character.setPenisCumStorage(cumProduction);
				}
				character.fillCumToMaxStorage();
			}

			if(Math.random()<=0.02f) {
				character.addCumModifier(FluidModifier.ADDICTIVE);
			}
			if(Math.random()<=0.02f) {
				character.addCumModifier(FluidModifier.HALLUCINOGENIC);
			}
		}

		// Vagina:
		if(character.hasVagina()) {
			if(character.hasFetish(Fetish.FETISH_PURE_VIRGIN)
					&& character.getHistory()!=Occupation.NPC_PROSTITUTE
					&& !character.hasPersonalityTrait(PersonalityTrait.LEWD)
					&& (!character.getHistory().isLowlife() || character.getAgeValue()==18)) {
				character.setVaginaVirgin(true);
				if(Math.random()<0.33f) {
					character.addPersonalityTrait(PersonalityTrait.INNOCENT);
				}
				int capacity = Util.random.nextInt((int) (Capacity.TWO_TIGHT.getMaximumValue(false)));
				character.setVaginaCapacity(capacity, true);

			} else {
				double random = Math.random();
				float chanceToBeDeflowered = character.hasFetish(Fetish.FETISH_PURE_VIRGIN)
						?(0.5f+(character.hasPersonalityTrait(PersonalityTrait.LEWD)?0.25f:0))
						:0.95f;
				if(random<chanceToBeDeflowered
						|| character.getHistory()==Occupation.NPC_PROSTITUTE) {
					character.setVaginaVirgin(false);
					character.setVaginaCapacity(character.getVaginaRawCapacityValue()*1.2f, true);

				} else {
					character.setVaginaVirgin(true);
				}
			}
			character.setVaginaWetness(character.getVaginaWetness().getValue() -1 + Util.random.nextInt(3)); // +1 or -1 either way
			if(Math.random()<=0.02f) {
				character.addGirlcumModifier(FluidModifier.ADDICTIVE);
			}
			if(Math.random()<=0.02f) {
				character.addGirlcumModifier(FluidModifier.HALLUCINOGENIC);
			}
		}

		generateAndApplySexCounts(character);

		character.setAssStretchedCapacity(character.getAssRawCapacityValue());
		character.setNippleStretchedCapacity(character.getNippleRawCapacityValue());
		character.setFaceStretchedCapacity(character.getFaceRawCapacityValue());
		character.setPenisStretchedCapacity(character.getPenisRawCapacityValue());
		character.setVaginaStretchedCapacity(character.getVaginaRawCapacityValue());

		character.getRace().applyRaceChanges(character.getBody());
		character.getSubspecies().applySpeciesChanges(character.getBody());
		character.getBody().calculateRace(character);
	}

	private static int getRandomSexCount(GameCharacter character) {
		// Count of how many times they have sex a year
		int baseCount = 1;
		int variation = 5;
		int days = (int) ChronoUnit.DAYS.between(character.getBirthday(), Main.game.getDateNow());
		float years = days/365;
		years += ((days%360)/360f);

		if(!character.hasFetish(Fetish.FETISH_PURE_VIRGIN) || !character.isVaginaVirgin()) {
			switch(character.getCorruptionLevel()) {
				case ZERO_PURE: // 1-5
					break;
				case ONE_VANILLA: // 10-20
					baseCount = 10;
					variation = 20;
					break;
				case TWO_HORNY: // 20-50
					baseCount = 20;
					variation = 50;
					break;
				case THREE_DIRTY: // 50-100
					baseCount = 50;
					variation = 100;
					break;
				case FOUR_LUSTFUL: // 100-250
					baseCount = 100;
					variation = 250;
					break;
				case FIVE_CORRUPT: // 250-500
					baseCount = 250;
					variation = 500;
					break;
			}
		}

		int count = baseCount + Util.random.nextInt(variation-baseCount+1);

		return Math.max(1, (int)(years*count));
	}

	public void randomiseEyeColour(GameCharacter character) {
		if (character.getBodyMaterial() == BodyMaterial.FLESH) {
			AbstractBodyCoveringType eyeCovering = character.getEyeCovering();
			Colour newEyeColour;
			if(Math.random()<0.15) {
				newEyeColour = Util.randomItemFrom(ColourListPresets.naturalIrisColours);
			} else if (Math.random()<0.20) {
				newEyeColour = Util.randomItemFrom(ColourListPresets.dyeIrisColours);
			} else if (Math.random()<0.25) {
				newEyeColour = Util.randomItemFrom(ColourListPresets.naturalDemonIrisColours);
			} else if (Math.random()<0.333) {
				newEyeColour = Util.randomItemFrom(ColourListPresets.dyeDemonIrisColours);
			} else if (Math.random()<0.50) {
				newEyeColour = Util.randomItemFrom(ColourListPresets.naturalPredatorIrisColours);
			} else {
				newEyeColour = Util.randomItemFrom(ColourListPresets.dyePredatorIrisColours);
			}
			if(Math.random()<0.03) {
				Colour newSecondaryEyeColour;
				if(Math.random()<0.15) {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.naturalIrisColours);
				} else if (Math.random()<0.20) {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.dyeIrisColours);
				} else if (Math.random()<0.25) {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.naturalDemonIrisColours);
				} else if (Math.random()<0.333) {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.dyeDemonIrisColours);
				} else if (Math.random()<0.50) {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.naturalPredatorIrisColours);
				} else {
					newSecondaryEyeColour = Util.randomItemFrom(ColourListPresets.dyePredatorIrisColours);
				}
				if (newSecondaryEyeColour != newEyeColour) {
					character.setEyeCovering(new Covering(
							eyeCovering,
							CoveringPattern.EYE_IRISES_HETEROCHROMATIC,
							newEyeColour, false,
							newSecondaryEyeColour, false));
				}
			} else {
				character.setEyeCovering(new Covering(eyeCovering, newEyeColour));
			}
		} else if ( character.getBodyMaterial() == BodyMaterial.RUBBER
				|| character.getBodyMaterial() == BodyMaterial.PLANT
				|| character.getBodyMaterial() == BodyMaterial.FUNGUS) {
			if(Math.random()<1f) {
				AbstractBodyCoveringType eyeCovering;
				Colour newEyeColour;

				eyeCovering = BodyCoveringType.getMaterialBodyCoveringType(character.getBodyMaterial(), BodyCoveringCategory.EYE_IRIS);
				newEyeColour = Util.randomItemFrom(eyeCovering.getNaturalColoursPrimary());
				System.err.println(eyeCovering.getName(character));
				System.err.println(newEyeColour.getName());
				character.getCovering(eyeCovering).setPrimaryColour(newEyeColour);
			}
		}
	}

	public void generateAndApplySexCounts(GameCharacter character) {
		boolean fullVirgin = character.isAssVirgin()
				&& character.isFaceVirgin()
				&& character.isNippleCrotchVirgin()
				&& character.isNippleVirgin()
				&& character.isPenisVirgin()
				&& character.isUrethraVirgin()
				&& character.isVaginaUrethraVirgin()
				&& character.isVaginaVirgin();

		// If this character is a pure virgin, they have no sexual experience:
		if(fullVirgin && (character.hasFetish(Fetish.FETISH_PURE_VIRGIN) || Math.random()<0.25f)) {
			return;
		}

		// Set dom/sub counts:
		int sexCount = getRandomSexCount(character);
		if(character.getFetishDesire(Fetish.FETISH_DOMINANT).isPositive()) {
			if(character.getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive()) {
				character.setSexAsSubCount(null, (int) ((sexCount/2)*(1-(Math.random()/2f))));
				character.setSexAsDomCount(null, (int) ((sexCount/2)*(1-(Math.random()/2f))));
			} else {
				character.setSexAsSubCount(null, (int) (sexCount*(0.05f+(Math.random()/10f))));
				character.setSexAsDomCount(null, (int) (sexCount*(1-(Math.random()/5f))));
			}

		} else if(character.getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive()) {
			character.setSexAsSubCount(null, (int) (sexCount*(1-(Math.random()/5f))));
			character.setSexAsDomCount(null, (int) (sexCount*(0.05f+(Math.random()/10f))));

		} else {
			character.setSexAsSubCount(null, (int) ((sexCount/2)*(1-(Math.random()/2f))));
			character.setSexAsDomCount(null, (int) ((sexCount/2)*(1-(Math.random()/2f))));
		}

		// Set consensual sex counts (based on sub/dom counts):
		if(character.getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive()) {
			character.setSexConsensualCount(null, (int) (character.getSexAsSubCount(null)*0.5f));
		} else {
			character.setSexConsensualCount(null, (int) (character.getSexAsSubCount(null)));
		}
		if(character.getFetishDesire(Fetish.FETISH_NON_CON_DOM).isPositive()) {
			character.setSexConsensualCount(null, character.getSexConsensualCount(null) + (int) (character.getSexAsDomCount(null)*0.5f));
		} else {
			character.setSexConsensualCount(null, character.getSexConsensualCount(null) + character.getSexAsDomCount(null));
		}

		if(Math.random()<0.2f && character.getSexConsensualCount(null)>50) { // Chance for this character to have been raped:
			character.setSexConsensualCount(null, character.getSexConsensualCount(null)-(1+Util.random.nextInt(5)));
		}


		// Set foreplay experience:
		character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.TONGUE, SexAreaOrifice.MOUTH), getRandomSexCount(character));
		if(character.hasBreasts()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.NIPPLE, SexAreaPenetration.TONGUE), getRandomSexCount(character));
		}
		if(character.getSexualOrientation().isAttractedToFeminine() && !character.getFetishDesire(Fetish.FETISH_BREASTS_OTHERS).isNegative()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.TONGUE, SexAreaOrifice.NIPPLE), getRandomSexCount(character));
		}
		if(character.hasVagina()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.VAGINA, SexAreaPenetration.FINGER), getRandomSexCount(character));
		}
		if(character.hasPenis()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.PENIS, SexAreaPenetration.FINGER), getRandomSexCount(character));
		}

		// Set sex experience:
		if(!character.isAssVirgin()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.ANUS, SexAreaPenetration.PENIS), getRandomSexCount(character));
			if(!character.getFetishDesire(Fetish.FETISH_ORAL_RECEIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.ANUS, SexAreaPenetration.TONGUE), getRandomSexCount(character));
			}
		}
		if(!character.isFaceVirgin()) {
			if(character.getSexualOrientation().isAttractedToFeminine() && !character.getFetishDesire(Fetish.FETISH_VAGINAL_GIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.TONGUE, SexAreaOrifice.VAGINA), getRandomSexCount(character));
			}
			if(!character.getFetishDesire(Fetish.FETISH_PENIS_RECEIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.MOUTH, SexAreaPenetration.PENIS), getRandomSexCount(character));
			} else {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.MOUTH, SexAreaPenetration.PENIS), 1+Util.random.nextInt(5));
			}
		}
		if(!character.isNippleCrotchVirgin()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.NIPPLE_CROTCH, SexAreaPenetration.PENIS), getRandomSexCount(character));
		}
		if(!character.isNippleVirgin()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.NIPPLE, SexAreaPenetration.PENIS), getRandomSexCount(character));
		}
		if(!character.isPenisVirgin()) {
			boolean found = false;
			if(character.getSexualOrientation().isAttractedToFeminine() && !character.getFetishDesire(Fetish.FETISH_VAGINAL_GIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.PENIS, SexAreaOrifice.VAGINA), getRandomSexCount(character));
				found = true;
			}
			if(!character.getFetishDesire(Fetish.FETISH_ANAL_GIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.PENIS, SexAreaOrifice.ANUS), getRandomSexCount(character));
				found = true;
			}
			if(!character.getFetishDesire(Fetish.FETISH_ORAL_RECEIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.PENIS, SexAreaOrifice.MOUTH), getRandomSexCount(character));
				found = true;
			}
			if(!found) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaPenetration.PENIS, SexAreaOrifice.VAGINA), 1+Util.random.nextInt(5));
			}
		}
		if(!character.isUrethraVirgin()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.URETHRA_PENIS, SexAreaPenetration.PENIS), getRandomSexCount(character));
		}
		if(!character.isVaginaVirgin()) {
			if(!character.getFetishDesire(Fetish.FETISH_PENIS_RECEIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.VAGINA, SexAreaPenetration.PENIS), getRandomSexCount(character));
			} else {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.VAGINA, SexAreaPenetration.PENIS), 1+Util.random.nextInt(5));
			}
			if(!character.getFetishDesire(Fetish.FETISH_ORAL_RECEIVING).isNegative()) {
				character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.VAGINA, SexAreaPenetration.TONGUE), getRandomSexCount(character));
			}
		}
		if(!character.isVaginaUrethraVirgin()) {
			character.setSexCount(null, new SexType(SexParticipantType.NORMAL, SexAreaOrifice.URETHRA_VAGINA, SexAreaPenetration.PENIS), getRandomSexCount(character));
		}
	}

	/**
	 * Calls generateItemsInInventory() with arguments based on the last time that method was called on this character.
	 */
	public void regenerateItemsInInventory(NPC character) {
		generateItemsInInventory(character, character.isGenerateExtraItems(), character.isGenerateDisposableClothing(), character.isGenerateExtraClothing());
	}

	/**
	 * This method will always generate items, clothing, and weapons based on the character's items that are rewarded to the player via the method getLootItems()
	 *
	 * @param character The character who is to receive items and clothing in their inventory.
	 * @param generateExtraItems true if you want this character to generate items that are not awarded via their getLootItems() method.  i.e. <b>pills</b>
	 * @param generateDisposableClothing true if you want this character to generate disposable clothing that is not awarded via their getLootItems() method. i.e. <b>condoms</b>
	 * @param generateExtraClothing true if you want this character to generate clothing that is not awarded via their getLootItems() method.  i.e. <b>BDSM gear and sex toys</b>
	 */
	public void generateItemsInInventory(NPC character, boolean generateExtraItems, boolean generateDisposableClothing, boolean generateExtraClothing) {
		character.setGenerateExtraItems(generateExtraItems);
		character.setGenerateDisposableClothing(generateDisposableClothing);
		character.setGenerateExtraClothing(generateExtraClothing);

		List<AbstractCoreItem> items;
		List<AbstractCoreItem> itemsRemoved = new ArrayList<>();

		for(int i=0; i<Util.random.nextInt(4)+2; i++) {
			items = new ArrayList<>(character.getLootItems());
			if(!items.isEmpty()) {
				AbstractCoreItem item = items.get(Util.random.nextInt(items.size()));
				if(!itemsRemoved.contains(item)) {
					if(item instanceof AbstractItem
							&& ((AbstractItem)item).getItemType() == ItemType.getLoreBook(character.getSubspecies())) {
						itemsRemoved.add(item);
					}
					if (item instanceof AbstractItem) {
						character.addItem((AbstractItem) item, false);

					} else if (item instanceof AbstractClothing) {
						character.addClothing((AbstractClothing) item, false);

					} else if (item instanceof AbstractWeapon) {
						character.addWeapon((AbstractWeapon) item, false);
					}
				}
			}
		}

		//**** ITEMS ****//

		// Pills:
		if(character.getFetishDesire(Fetish.FETISH_PREGNANCY).isPositive()
				&& !character.isPregnant()
				&& !character.hasIncubationLitter(SexAreaOrifice.VAGINA)
				&& character.hasVagina()) {
			character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_fertility"), 2+Util.random.nextInt(4), false, false);
			if(Math.random()<0.2f) {
				character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_broodmother"), 1+Util.random.nextInt(2), false, false);
			}
		}
		if(character.getFetishDesire(Fetish.FETISH_IMPREGNATION).isPositive() && character.hasPenisIgnoreDildo()) {
			character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_fertility"), 2+Util.random.nextInt(4), false, false);
			if(Math.random()<0.2f) {
				character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_broodmother"), 1+Util.random.nextInt(2), false, false);
			}
		}
		if(character.getFetishDesire(Fetish.FETISH_PREGNANCY).isNegative()
				&& !character.isPregnant()
				&& !character.hasIncubationLitter(SexAreaOrifice.VAGINA)
				&& character.hasVagina()) {
			character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_sterility"), 2+Util.random.nextInt(4), false, false);
		}
		if(character.getFetishDesire(Fetish.FETISH_IMPREGNATION).isNegative() && character.hasPenisIgnoreDildo()) {
			character.addItem(Main.game.getItemGen().generateItem("innoxia_pills_sterility"), 2+Util.random.nextInt(4), false, false);
		}

		//**** CLOTHING ****//

		// Condoms:
		if(character.hasPenis()
				&& !character.getFetishDesire(Fetish.FETISH_PENIS_GIVING).isNegative() // doesn't mind using penis
				&& (character.getFetishDesire(Fetish.FETISH_CUM_STUD).isNegative() // dislikes cum play or impregnating others
				|| character.getFetishDesire(Fetish.FETISH_IMPREGNATION).isNegative())) {
			String condomType = "innoxia_penis_condom";
			int cumQuantity = character.getPenisRawOrgasmCumQuantity();
			if(cumQuantity>CumProduction.FIVE_HUGE.getMaximumValue()) {
				condomType = "innoxia_penis_condom_strong";
			} else if(cumQuantity>CumProduction.SIX_EXTREME.getMaximumValue()) {
				condomType = "innoxia_penis_condom_super_strong";
			}
			Colour condomColour = ClothingType.getClothingTypeFromId(condomType).getColourReplacement(0).getRandomOfDefaultColours();
			//character.addClothing(Main.game.getItemGen().generateClothing(condomType, condomColour, false), 1+Util.random.nextInt(3), false, false);
		}
		if(!character.getFetishDesire(Fetish.FETISH_PENIS_RECEIVING).isNegative() // doesn't mind receiving cock
				&& (character.getFetishDesire(Fetish.FETISH_CUM_ADDICT).isNegative()  // dislikes others' cum or has vagina and dislikes getting pregnant
				|| (character.hasVagina() && character.getFetishDesire(Fetish.FETISH_PREGNANCY).isNegative()))) {
			Colour condomColour = ClothingType.getClothingTypeFromId("innoxia_penis_condom").getColourReplacement(0).getRandomOfDefaultColours();
			//character.addClothing(Main.game.getItemGen().generateClothing("innoxia_penis_condom", condomColour, false), 1+Util.random.nextInt(3), false, false);
		}

		int maxClothingCount = 1;
		// Spawning additional clothing:
		List<AbstractClothingType> availableClothing = new ArrayList<>();
		// Only bondage fetishists spawn with BDSM clothing:
		if(character.hasFetish(Fetish.FETISH_BONDAGE_APPLIER)) {
			maxClothingCount+=1;
			for(AbstractClothingType ct : ClothingType.getAllClothingInSet(SetBonus.getSetBonusFromId("innoxia_bdsm"))) {
				InventorySlot defaultSlot = ct.getEquipSlots().get(0);
				if(defaultSlot!=InventorySlot.VAGINA && defaultSlot!=InventorySlot.PENIS && defaultSlot!=InventorySlot.ANUS && defaultSlot!=InventorySlot.NIPPLE && defaultSlot!=InventorySlot.GROIN) {
					availableClothing.add(ct); // Do not add clothing types which are sex toys, as conditionals for those are added in the next logic block
				}
			}
		}
		// Sex toys:
		if(character.getCorruptionLevel().getMinimumValue()>=CorruptionLevel.THREE_DIRTY.getMinimumValue()) { // Only 'dirty' corruption characters carry sex toys around
			maxClothingCount+=1;
			for(AbstractClothingType ct : ClothingType.getAllClothing()) {
				InventorySlot defaultSlot = ct.getEquipSlots().get(0);
				if(ct.getDefaultItemTags().contains(ItemTag.ENABLE_SEX_EQUIP)) {
					// Conditionals for equipping sex toys are if this character is not averse to using the associated area
					// (choosing to equip them is handled in NPC.getSexClothingToEquip(), so it's ok to give them clothing which they might not want to equip)
					if(defaultSlot==InventorySlot.VAGINA) {
						if(!character.getFetishDesire(Fetish.FETISH_VAGINAL_GIVING).isNegative()) {
							availableClothing.add(ct);
						}
					} else if(defaultSlot==InventorySlot.PENIS && !ct.getDefaultItemTags().contains(ItemTag.CONDOM) && !ct.getDefaultItemTags().contains(ItemTag.DILDO_OTHER)) { //Don't equip dildos on others
						if(!character.getFetishDesire(Fetish.FETISH_PENIS_RECEIVING).isNegative()
								&& (Collections.disjoint(ct.getDefaultItemTags(), Util.newArrayListOfValues(ItemTag.CHASTITY, ItemTag.PREVENTS_ERECTION_PHYSICAL, ItemTag.PREVENTS_ERECTION_OTHER))
								|| character.getFetishDesire(Fetish.FETISH_DENIAL).isPositive())) {
							availableClothing.add(ct);
						}
					} else if(defaultSlot==InventorySlot.ANUS) {
						if(Main.game.isAnalContentEnabled() && !character.getFetishDesire(Fetish.FETISH_ANAL_GIVING).isNegative()) {
							availableClothing.add(ct);
						}
					} else if(defaultSlot==InventorySlot.NIPPLE) {
						if(!character.getFetishDesire(Fetish.FETISH_BREASTS_OTHERS).isNegative()) {
							availableClothing.add(ct);
						}
					} else if(defaultSlot==InventorySlot.GROIN) {
						if(ct.getDefaultItemTags().contains(ItemTag.CHASTITY) && character.getFetishDesire(Fetish.FETISH_DENIAL).isPositive()) {
							availableClothing.add(ct); // Only add chastity devices into groin slot
						}
					}
				}
			}
		}

		availableClothing.removeIf(ct->ct.getDefaultItemTags().contains(ItemTag.NO_RANDOM_SPAWN) || ct.getDefaultItemTags().contains(ItemTag.CHEAT_ITEM) || ct.getRarity()==Rarity.QUEST);

		// Adding clothing to inventory:
		maxClothingCount+=Util.random.nextInt(3);
		Collections.shuffle(availableClothing);
		for(AbstractClothingType ct : availableClothing) {
			character.addClothing(Main.game.getItemGen().generateClothing(ct, false), 1, false, false);
			maxClothingCount--;
			if(maxClothingCount<=0) {
				break;
			}
		}
	}

	/**
	 * Sets the History for the supplied character.
	 * @param character
	 */
	public void setHistoryAndPersonality(GameCharacter character, boolean lowlife) {

		//TODO Set personality based on history. (Or vice-versa, but one should lead to the other.)

		if(lowlife) {
			// High chance to be slovenly:
			if(Math.random()<0.0625f) {
				character.addPersonalityTrait(PersonalityTrait.SLOVENLY);
			}

			double prostituteChance = 0.15f; // Base 0.15% chance for any random to be a prostitute.

			if(character.isFeminine()) {
				prostituteChance += 0.10f; // Bonus for femininity
			}

			prostituteChance += Math.min((character.body.getBreast().getRawSizeValue()-7)*0.02f, 0.35f); // Compare breast size to average.

			if(character.hasPenis()) {
				prostituteChance += Math.min((character.body.getPenis().getRawLengthValue()-5)*0.01f, 0.10f); // Scaling based off of cock size. Very small cocks are a penalty.
			}

			if(character.hasVagina()) {
				prostituteChance += 0.15f; // Bonus for vagina.
			}

			if(character.body.getBreast().getNipples().getOrificeNipples().getRawCapacityValue() >= 4) {
				prostituteChance += 0.05f; //Bonus for fuckable nipples.
			}

			if(character.hasFetish(Fetish.FETISH_PURE_VIRGIN)) {
				prostituteChance = 0.03f; // addFetishes() can be called before or after this method. This is a catch for the case where addFetishes() is called before.
			}

			prostituteChance = Math.min(prostituteChance, 0.3f); // Prostitutes can only ever spawn at a maximum of a 30% chance.

			if (Math.random() < prostituteChance) {
				character.setHistory(Occupation.NPC_PROSTITUTE);
				initProstitute(character);

			} else {
				character.setHistory(Occupation.NPC_MUGGER);
			}

		} else {
			List<Occupation> histories = Occupation.getAvailableHistories(character);
			histories.removeIf((his) -> his.isLowlife());
			histories.add(Occupation.NPC_FARMER); // Extra farmer chance
			character.setHistory(Util.randomItemFrom(histories));
		}
	}

	public static void initProstitute(GameCharacter character) {
		character.removePersonalityTrait(PersonalityTrait.PRUDE);
		character.removePersonalityTrait(PersonalityTrait.INNOCENT);

		character.setAssVirgin(false);
		character.setAssCapacity(character.getAssRawCapacityValue()
						* 1.2f,
				true);

		if(character.hasVagina()) {
			character.setVaginaVirgin(false);
			character.setVaginaCapacity(character.getVaginaRawCapacityValue()
							* 1.2f,
					true);
		}

		if(character.hasPenis()) {
			character.setPenisVirgin(false);
		}

		character.setSexualOrientation(SexualOrientation.AMBIPHILIC);
		character.setName(Name.getRandomProstituteTriplet());
		character.useItem(Main.game.getItemGen().generateItem("innoxia_pills_sterility"),
				character,
				false);
	}

	private static List<AbstractFetish> getAllowedFetishes(GameCharacter character) {
		List<AbstractFetish> allowedFetishes = new ArrayList<>();

		for(AbstractFetish f : Fetish.getAllFetishes()) {
			if (Fetish.FETISH_PURE_VIRGIN.equals(f)) {
				if (character.hasVagina() && (character.getHistory() != Occupation.NPC_PROSTITUTE || Math.random()<=0.25f))
					allowedFetishes.add(f);
			} else if (Fetish.FETISH_PREGNANCY.equals(f) || Fetish.FETISH_VAGINAL_RECEIVING.equals(f)) {
				if (character.hasVagina())
					allowedFetishes.add(f);
			} else if (Fetish.FETISH_IMPREGNATION.equals(f)) {
				if (character.hasPenis() && character.sexualOrientation != SexualOrientation.ANDROPHILIC)
					allowedFetishes.add(f);
			} else if (Fetish.FETISH_CUM_STUD.equals(f) || Fetish.FETISH_PENIS_GIVING.equals(f)) {
				if (character.hasPenis())
					allowedFetishes.add(f);
			} else if (Fetish.FETISH_BREASTS_SELF.equals(f)) {
				if (character.hasBreasts())
					allowedFetishes.add(f);
			} else {
				if (f.getFetishesForAutomaticUnlock().isEmpty() && f.isContentEnabled())
					allowedFetishes.add(f);
			}
		}
		return allowedFetishes;
	}

	/**
	 * @param character The character to add fetishes to.
	 * @param exclusions Any fetishes that should not be modified.
	 */
	public void addFetishes(GameCharacter character, AbstractFetish... exclusions) {

		List<AbstractFetish> availableFetishes = getAllowedFetishes(character);
		// If player preference for a fetish is set to 'always', then always allow it to be added:
		for(AbstractFetish fetish : Fetish.getAllFetishes()) {
			if(Main.getProperties().fetishPreferencesMap.get(fetish)==FetishPreference.SIX_ALWAYS.getValue()) {
				availableFetishes.add(fetish);
			}
		}

		// Remove existing fetishes and exclusions:
		availableFetishes.removeAll(character.getFetishes(false));
		availableFetishes.removeAll(Arrays.asList(exclusions));

		int[] numberProb = new int[] {1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
		int numberOfFetishes = Util.randomItemFrom(numberProb) - character.getFetishes(false).size();

		int fetishesAssigned = 0;

		if(((character.getMother()!=null && character.getMother().isPlayer()) || (character.getFather()!=null && character.getFather().isPlayer()))) {
			if(Main.getProperties().hasValue(PropertyValue.incestContent) && Math.random()>0.5f) {
				character.addFetish(Fetish.FETISH_INCEST);
				availableFetishes.remove(Fetish.FETISH_INCEST);
				fetishesAssigned++;
			}
		} else { // If not offspring, give them the chance for TF fetish and the Kink giving fetish:
			if(Math.random() < (Main.getProperties().forcedTFPercentage/100f)) {
				character.addFetish(Fetish.FETISH_TRANSFORMATION_GIVING);
				fetishesAssigned++;
			}
			availableFetishes.remove(Fetish.FETISH_TRANSFORMATION_GIVING);

			if(Math.random() < (Main.getProperties().forcedFetishPercentage/100f)) {
				character.addFetish(Fetish.FETISH_KINK_GIVING);
				fetishesAssigned++;
			}
			availableFetishes.remove(Fetish.FETISH_KINK_GIVING);
		}

		Map<AbstractFetish, Integer> fetishMap = new HashMap<>();
		Map<AbstractFetish, Map<String, Integer>> raceModifiers = character.getRace().getRacialFetishModifiers();
		for(AbstractFetish fetish : availableFetishes) {
			if(Main.getProperties().fetishPreferencesMap.get(fetish)==FetishPreference.SIX_ALWAYS.getValue()) {
				character.addFetish(fetish);

			} else {
				int weight = 0;
				if(raceModifiers.containsKey(fetish)) {
					// Racial modifier acts as a multiplier so fetishes can be disabled by race or player preferences
					weight = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(fetish)).getLove() * raceModifiers.get(fetish).getOrDefault("love", 1);
				} else {
					weight = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(fetish)).getLove();
				}
				if(weight!=0) {
					fetishMap.put(fetish, weight);
				}
			}
		}
		while(fetishesAssigned < numberOfFetishes && !fetishMap.isEmpty()) {
			AbstractFetish f = Util.getRandomObjectFromWeightedMap(fetishMap);
			character.addFetish(f);
			fetishMap.remove(f);
			fetishesAssigned++;
		}

		generateDesires(character);
	}

	public void generateDesires(GameCharacter character) {

		List<AbstractFetish> availableFetishes = getAllowedFetishes(character);
		availableFetishes.removeAll(character.getFetishes(false));
		for(AbstractFetish f : character.getFetishes(false)) {
			// Related fetishes cannot be loved and disliked at the same time:
			if (Fetish.FETISH_PREGNANCY.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_VAGINAL_RECEIVING);
			} else if (Fetish.FETISH_IMPREGNATION.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_VAGINAL_GIVING);
			}
		}

		// Desires:
		int[] posDesireProb = new int[] {1, 1, 2, 2, 2, 3, 3};
		int[] negDesireProb = new int[] {2, 2, 3, 3, 3, 4, 4};
		int numberOfPositiveDesires = Util.randomItemFrom(posDesireProb);
		int numberOfNegativeDesires = Util.randomItemFrom(negDesireProb);

		int desiresAssigned = 0;
		List<AbstractFetish> fetishesLiked = new ArrayList<>(character.getFetishes(false));

		Map<AbstractFetish, Integer> desireMap = new HashMap<>();
		Map<AbstractFetish, Map<String, Integer>> raceModifiers = character.getRace().getRacialFetishModifiers();
		for(AbstractFetish fetish : availableFetishes) {
			int weight = 0;
			if(raceModifiers.containsKey(fetish)) {
				// Racial modifier acts as a multiplier so fetishes can be disabled by race or player preferences
				weight = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(fetish)).getLike() * raceModifiers.get(fetish).getOrDefault("like", 1);
			} else {
				weight = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(fetish)).getLike();
			}
			if(weight!=0) {
				desireMap.put(fetish, weight);
			}
		}
		while(desiresAssigned < numberOfPositiveDesires && !desireMap.isEmpty()) {
			AbstractFetish f = Util.getRandomObjectFromWeightedMap(desireMap);
			character.setFetishDesire(f, FetishDesire.THREE_LIKE);
			availableFetishes.remove(f);
			desireMap.remove(f);
			fetishesLiked.add(f);
			desiresAssigned++;
		}

		// Disliked fetishes:
		// Related fetishes cannot be liked and disliked at the same time:
		for(AbstractFetish f : fetishesLiked) {
			if (Fetish.FETISH_VAGINAL_RECEIVING.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_PENIS_RECEIVING);
			} else if (Fetish.FETISH_VAGINAL_GIVING.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_PENIS_GIVING);
			} else if (Fetish.FETISH_PREGNANCY.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_VAGINAL_RECEIVING);
				availableFetishes.remove(Fetish.FETISH_PENIS_RECEIVING);
				availableFetishes.remove(Fetish.FETISH_CUM_ADDICT);
			} else if (Fetish.FETISH_IMPREGNATION.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_VAGINAL_GIVING);
				availableFetishes.remove(Fetish.FETISH_PENIS_GIVING);
				availableFetishes.remove(Fetish.FETISH_CUM_STUD);
			} else if (Fetish.FETISH_NON_CON_SUB.equals(f)) {
				availableFetishes.remove(Fetish.FETISH_SUBMISSIVE);
			}
		}

		desiresAssigned = 0;
		if(character instanceof Cultist || character instanceof DominionSuccubusAttacker) { // Cultists and succubus attackers like raping
			availableFetishes.remove(Fetish.FETISH_NON_CON_DOM);
		}
		if(character.getSexualOrientation()!=SexualOrientation.GYNEPHILIC) {
			availableFetishes.remove(Fetish.FETISH_PENIS_RECEIVING);
		}
		if(character.getSexualOrientation()!=SexualOrientation.ANDROPHILIC) {
			availableFetishes.remove(Fetish.FETISH_VAGINAL_GIVING);
			availableFetishes.remove(Fetish.FETISH_BREASTS_OTHERS);
		}

		// Remove 'natural' fetish dislikes:
		availableFetishes.remove(Fetish.FETISH_CUM_STUD);
		availableFetishes.remove(Fetish.FETISH_PENIS_GIVING);
		availableFetishes.remove(Fetish.FETISH_VAGINAL_RECEIVING);

		Map<AbstractFetish, Integer> negativeMap = new HashMap<>();
		for(AbstractFetish fetish : availableFetishes) {
			int weight = 0;
			FetishPreference fp = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(fetish));
			if(raceModifiers.containsKey(fetish)) {
				// Racial modifier acts as a multiplier so fetishes can be disabled by race or player preferences
				weight = fp.getDislike() * raceModifiers.get(fetish).getOrDefault("dislike", 1);
				weight += fp.getHate() * raceModifiers.get(fetish).getOrDefault("hate", 1);
			} else {
				weight = fp.getDislike() + fp.getHate();
			}
			if(weight!=0) {
				negativeMap.put(fetish, weight);
			}
		}
		while(desiresAssigned < numberOfNegativeDesires && !negativeMap.isEmpty()) {
			AbstractFetish f = Util.getRandomObjectFromWeightedMap(negativeMap);
			int rnd = Util.random.nextInt(negativeMap.get(f))+1;
			int weight = FetishPreference.valueOf(Main.getProperties().fetishPreferencesMap.get(f)).getHate();
			if(raceModifiers.containsKey(f)) {
				weight *= raceModifiers.get(f).getOrDefault("hate", 1);
			}
			character.setFetishDesire(f, rnd>weight?FetishDesire.ONE_DISLIKE:FetishDesire.ZERO_HATE);
			if(f == Fetish.FETISH_DOMINANT) {
				negativeMap.remove(Fetish.FETISH_SUBMISSIVE);
			}
			if(f == Fetish.FETISH_SUBMISSIVE) {
				negativeMap.remove(Fetish.FETISH_DOMINANT);
			}
			negativeMap.remove(f);
			desiresAssigned++;
		}
	}


	public void equipClothingFromOutfitId(GameCharacter character, String outfitId) {
		equipClothingFromOutfit(character, OutfitType.getOutfitTypeFromId(outfitId), EquipClothingSetting.getAllClothingSettings());
	}

	public void equipClothingFromOutfitFolderId(GameCharacter character, OutfitType outfitType, String folderId, List<EquipClothingSetting> settings) {
		equipClothingFromOutfits(character, OutfitType.getOutfitsFromIdStart(folderId), outfitType, settings);
	}

	public void equipClothingFromOutfitType(GameCharacter character, OutfitType outfitType, List<EquipClothingSetting> settings) {
		equipClothingFromOutfits(character, OutfitType.getAllOutfits(), outfitType, settings);
	}

	public void equipClothingFromOutfitType(GameCharacter character, OutfitType outfitType) {
		equipClothingFromOutfits(character, OutfitType.getAllOutfits(), outfitType, EquipClothingSetting.getAllClothingSettings());
	}

	private void equipClothingFromOutfits(GameCharacter character, List<AbstractOutfit> availableOutfits, OutfitType outfitType, List<EquipClothingSetting> settings) {
		Map<AbstractOutfit, Integer> weightedOutfits = new HashMap<>();

		for(AbstractOutfit outfit : availableOutfits) {
			if(outfit.isAvailableForCharacter(outfitType, character)) {
				weightedOutfits.put(outfit, outfit.getWeight());
			}
		}

		if(weightedOutfits.isEmpty()) {
			equipClothingFromOutfit(character, null, settings);
		} else {
			equipClothingFromOutfit(character, Util.getRandomObjectFromWeightedMap(weightedOutfits), settings);
		}
	}

	public void equipClothingFromOutfitId(GameCharacter character, String outfitId, List<EquipClothingSetting> settings) {
		equipClothingFromOutfit(character, OutfitType.getOutfitTypeFromId(outfitId), settings);
	}

	public void equipClothingFromOutfit(GameCharacter character, AbstractOutfit outfit, List<EquipClothingSetting> settings) {
		if(outfit!=null) {
			try {
				outfit.applyOutfit(character, settings);
				return;
			} catch (XMLLoadException e) {
				System.err.println("Outfit '"+outfit.getName()+"' could not be applied in CharacterUtils equipClothing(). Proceeding to randomly generate outfit...");
				e.printStackTrace();
			}
		}

		Colour primaryColour = ColourListPresets.ALL.get(Util.random.nextInt(ColourListPresets.ALL.size()));
		Colour secondaryColour = ColourListPresets.ALL.get(Util.random.nextInt(ColourListPresets.ALL.size()));
		Colour lingerieColour = ColourListPresets.LINGERIE.get(Util.random.nextInt(ColourListPresets.LINGERIE.size()));

		// Remove exposing underwear if replaceUnsuitableClothing and is exposed:
		if(settings.contains(EquipClothingSetting.REPLACE_CLOTHING)
				&& character.getClothingInSlot(InventorySlot.GROIN)!=null
				&& (character.hasStatusEffect(StatusEffect.EXPOSED) || character.hasStatusEffect(StatusEffect.EXPOSED_PLUS_BREASTS))) {
			character.unequipClothingIntoVoid(character.getClothingInSlot(InventorySlot.GROIN), true, character);
		}

		List<InventorySlot> inventorySlotsInPriorityOrder = new ArrayList<>();
		inventorySlotsInPriorityOrder.add(InventorySlot.TORSO_UNDER); // Torso needs to be randomly decided first, to give girls a chance to wear a dress.
		for(InventorySlot slot : InventorySlot.values()) {
			if(slot!=InventorySlot.TORSO_UNDER) {
				inventorySlotsInPriorityOrder.add(slot);
			}
		}

		if((character.isFeminine() && !character.hasFetish(Fetish.FETISH_CROSS_DRESSER)) || (!character.isFeminine() && character.hasFetish(Fetish.FETISH_CROSS_DRESSER))) {
			for(InventorySlot slot : inventorySlotsInPriorityOrder) {
				if(settings.contains(EquipClothingSetting.REPLACE_CLOTHING)) {
					if(character.getClothingInSlot(slot)!=null) {
						if(character.getClothingInSlot(slot).getClothingType().getFemininityRestriction() == Femininity.MASCULINE) {
							character.unequipClothingIntoVoid(character.getClothingInSlot(slot), true, character);
						}
					}
				}

				if(slot==InventorySlot.LEG
						&& character.getClothingInSlot(InventorySlot.TORSO_UNDER)!=null
						&& character.getClothingInSlot(InventorySlot.TORSO_UNDER).getItemTags().contains(ItemTag.DRESS)) {
					// Don't add leg clothing if dress has been added
				} else {
					if((slot.isCoreClothing() || Math.random()>0.75f || (slot.isJewellery() && character.getBodyMaterial().isRequiresPiercing())) && !character.isSlotIncompatible(slot) && character.getClothingInSlot(slot)==null) {
						List<AbstractClothingType> clothingToUse = ClothingType.getCommonClothingMapFemaleIncludingAndrogynous().get(slot);
						if(character.getHistory()==Occupation.NPC_PROSTITUTE) {
							clothingToUse = ClothingType.getSuitableFeminineClothing().get(Occupation.NPC_PROSTITUTE);
						}
						if(!clothingToUse.isEmpty()) {
							BodyPartClothingBlock block = slot.getBodyPartClothingBlock(character);
							clothingToUse = clothingToUse.stream().filter((c) ->
									!c.isDefaultSlotCondom(slot)
											&& (block==null || !Collections.disjoint(c.getItemTags(slot), block.getRequiredTags()))
											&& (!character.hasBreastsCrotch()
											|| character.getLegConfiguration()==LegConfiguration.QUADRUPEDAL  // Taurs crotch boobs are not concealed by stomach clothing, so don't bother
											|| c.isConcealsSlot(character, InventorySlot.STOMACH)
											|| c.getEquipSlots().get(0)!=InventorySlot.TORSO_UNDER || c.getEquipSlots().get(0)!=InventorySlot.TORSO_OVER
											|| character.getInventorySlotsConcealed(Main.game.getPlayer()).containsKey(InventorySlot.STOMACH))
							).collect(Collectors.toList());


							if(!clothingToUse.isEmpty()) {
								AbstractClothingType ct = getClothingTypeForSlot(character, slot, clothingToUse);

								if(ct!=null) {
									AbstractClothing clothingToAdd = Main.game.getItemGen().generateClothing(
											ct,
											(slot == InventorySlot.GROIN || slot==InventorySlot.CHEST || slot==InventorySlot.SOCK
													? ct.getColourReplacement(0).getDefaultColours().contains(lingerieColour)?lingerieColour:ct.getColourReplacement(0).getRandomOfDefaultColours()
													: (slot.isCoreClothing()
													?ct.getColourReplacement(0).getDefaultColours().contains(primaryColour)?primaryColour:ct.getColourReplacement(0).getRandomOfDefaultColours()
													:ct.getColourReplacement(0).getDefaultColours().contains(secondaryColour)?secondaryColour:ct.getColourReplacement(0).getRandomOfDefaultColours())),
											false);
									character.equipClothingFromNowhere(clothingToAdd, slot, true, character);
								}
							}
						}
					}
				}
			}

		} else {
			for(InventorySlot slot : inventorySlotsInPriorityOrder) {
				if(settings.contains(EquipClothingSetting.REPLACE_CLOTHING)) {
					if(character.getClothingInSlot(slot)!=null) {
						if(character.getClothingInSlot(slot).getClothingType().getFemininityRestriction() == Femininity.FEMININE) {
							character.unequipClothingIntoVoid(character.getClothingInSlot(slot), true, character);
						}
					}
				}

				if(slot==InventorySlot.LEG
						&& character.getClothingInSlot(InventorySlot.TORSO_UNDER)!=null
						&& character.getClothingInSlot(InventorySlot.TORSO_UNDER).getClothingType().toString().contains("DRESS")) {//TODO please don't do this //BE QUIET
					// Don't add leg clothing if dress has been added

				} else {
					if((slot.isCoreClothing() || Math.random()>0.75f || (slot.isJewellery() && character.getBodyMaterial().isRequiresPiercing())) && !character.isSlotIncompatible(slot) && character.getClothingInSlot(slot)==null) {

						List<AbstractClothingType> clothingToUse = ClothingType.getCommonClothingMapMaleIncludingAndrogynous().get(slot);

						if(!clothingToUse.isEmpty()) {
							BodyPartClothingBlock block = slot.getBodyPartClothingBlock(character);
							clothingToUse = clothingToUse.stream().filter((c) ->
									!c.isDefaultSlotCondom(slot)
											&& (block==null || !Collections.disjoint(c.getItemTags(slot), block.getRequiredTags()))
											&& (!character.hasBreastsCrotch()
											|| character.getLegConfiguration()==LegConfiguration.QUADRUPEDAL  // Taurs crotch boobs are not concealed by stomach clothing, so don't bother
											|| c.isConcealsSlot(character, InventorySlot.STOMACH)
											|| c.getEquipSlots().get(0)!=InventorySlot.TORSO_UNDER || c.getEquipSlots().get(0)!=InventorySlot.TORSO_OVER
											|| character.getInventorySlotsConcealed(Main.game.getPlayer()).containsKey(InventorySlot.STOMACH))
							).collect(Collectors.toList());

							if(!clothingToUse.isEmpty()) {
								AbstractClothingType ct = getClothingTypeForSlot(character, slot, clothingToUse);

								if(ct!=null) {
									AbstractClothing clothingToAdd = Main.game.getItemGen().generateClothing(
											ct,
											(slot == InventorySlot.GROIN || slot==InventorySlot.CHEST || slot==InventorySlot.SOCK
													? ct.getColourReplacement(0).getDefaultColours().contains(lingerieColour)?lingerieColour:ct.getColourReplacement(0).getRandomOfDefaultColours()
													: (slot.isCoreClothing()
													?ct.getColourReplacement(0).getDefaultColours().contains(primaryColour)?primaryColour:ct.getColourReplacement(0).getRandomOfDefaultColours()
													:ct.getColourReplacement(0).getDefaultColours().contains(secondaryColour)?secondaryColour:ct.getColourReplacement(0).getRandomOfDefaultColours())),
											false);
									character.equipClothingFromNowhere(clothingToAdd, slot, true, character);
								}
							}

						}
					}
				}
			}
		}
	}

	public void equipPiercings(GameCharacter character, boolean replaceUnsuitableClothing) {
		Map<InventorySlot, List<AbstractClothingType>> clothingMap = ClothingType.getCommonClothingMapMaleIncludingAndrogynous();

		if(character.isFeminine() || character.hasFetish(Fetish.FETISH_CROSS_DRESSER)) {
			clothingMap = ClothingType.getCommonClothingMapFemaleIncludingAndrogynous();
		}

		for(InventorySlot slot : InventorySlot.getPiercingSlots()) {
			if(replaceUnsuitableClothing) {
				if(character.getClothingInSlot(slot)!=null) {
					if(character.getClothingInSlot(slot).getClothingType().getFemininityRestriction() == Femininity.MASCULINE) {
						character.unequipClothingIntoVoid(character.getClothingInSlot(slot), true, character);
					}
				}
			}

			if(!character.isSlotIncompatible(slot) && character.getClothingInSlot(slot)==null) {
				if(!clothingMap.get(slot).isEmpty()) {

					BodyPartClothingBlock block = slot.getBodyPartClothingBlock(character);
					List<AbstractClothingType> clothingToUse = clothingMap.get(slot);
					clothingToUse = clothingToUse.stream().filter((c) -> (block==null || !Collections.disjoint(c.getItemTags(slot), block.getRequiredTags()))).collect(Collectors.toList());

					AbstractClothingType ct = getClothingTypeForSlot(character, slot, clothingToUse);

					if(ct!=null) {
						character.equipClothingFromNowhere(Main.game.getItemGen().generateClothing(ct, false), slot, true, character);
					}
				}
			}
		}
	}

	private static AbstractClothingType getClothingTypeForSlot(GameCharacter character, InventorySlot slot, List<AbstractClothingType> clothingOptions) {
		List<AbstractClothingType> availableClothing = new ArrayList<>();

		boolean canEquip=true;

		for(AbstractClothingType ct : clothingOptions) {
			if(!ct.getEquipSlots().contains(slot)) {
				continue;
			}
			canEquip=true;

			if(slot==InventorySlot.CHEST && !character.hasBreasts()) {
				canEquip = false;

			} else if(character.hasFetish(Fetish.FETISH_EXHIBITIONIST)) {
				AbstractClothing clothingExample = Main.game.getItemGen().generateClothing(ct);
				for(BlockedParts bp : clothingExample.getBlockedPartsMap(character, slot)) {
					boolean leavesAnusExposed = character.isCoverableAreaExposed(CoverableArea.ANUS) && !bp.blockedBodyParts.contains(CoverableArea.ANUS);
					boolean leavesNipplesExposed = character.isCoverableAreaExposed(CoverableArea.NIPPLES) && !bp.blockedBodyParts.contains(CoverableArea.NIPPLES);
					boolean leavesPenisExposed = !character.hasPenis() || (character.isCoverableAreaExposed(CoverableArea.PENIS) && !bp.blockedBodyParts.contains(CoverableArea.PENIS));
					boolean leavesVaginaExposed = !character.hasVagina() || (character.isCoverableAreaExposed(CoverableArea.VAGINA) && !bp.blockedBodyParts.contains(CoverableArea.VAGINA));
					//TODO check this:
					if(!clothingExample.isTransparent(slot) && (!leavesNipplesExposed || (!leavesAnusExposed || (!leavesPenisExposed && !leavesVaginaExposed)))) {
						canEquip = false;
					}
				}


			} else {
				AbstractClothing clothingExample = Main.game.getItemGen().generateClothing(ct);
				for(InventorySlot is : clothingExample.getIncompatibleSlots(character, slot)) {
					if(character.getClothingInSlot(is) != null) {
						canEquip = false;
					}
				}
			}

			if(canEquip) {
				availableClothing.add(ct);
			}
		}

		if(availableClothing.isEmpty()) {
			return null;

		} else {
			return Util.randomItemFrom(availableClothing);
		}
	}

	public void applyMakeup(GameCharacter character, boolean overrideExistingMakeup) {
		if(!character.getBodyMaterial().isAbleToWearMakeup()) {
			return;
		}

		if((character.isFeminine() && !character.hasFetish(Fetish.FETISH_CROSS_DRESSER)) || (!character.isFeminine() && character.hasFetish(Fetish.FETISH_CROSS_DRESSER))) {
			List<Colour> colours = Util.newArrayListOfValues(
					PresetColour.COVERING_NONE,
					PresetColour.COVERING_CLEAR,
					PresetColour.COVERING_RED,
					PresetColour.COVERING_PINK,
					PresetColour.COVERING_BLUE);

			if(character.getHistory()==Occupation.NPC_PROSTITUTE) {
				colours.remove(PresetColour.COVERING_NONE);
				colours.remove(PresetColour.COVERING_CLEAR);
			}

			Colour colourForCoordination = Util.randomItemFrom(colours);
			Colour colourForNails = Util.randomItemFrom(colours);

			character.setLipstick(new Covering(BodyCoveringType.MAKEUP_LIPSTICK, colourForCoordination));
			character.setEyeLiner(new Covering(BodyCoveringType.MAKEUP_EYE_LINER, PresetColour.COVERING_BLACK));
			character.setEyeShadow(new Covering(BodyCoveringType.MAKEUP_EYE_SHADOW, colourForCoordination));
			character.setBlusher(new Covering(BodyCoveringType.MAKEUP_BLUSHER, colourForCoordination));

			character.setHandNailPolish(new Covering(BodyCoveringType.MAKEUP_NAIL_POLISH_HANDS, colourForNails));
			character.setFootNailPolish(new Covering(BodyCoveringType.MAKEUP_NAIL_POLISH_FEET, colourForNails));

		} else {
			// Masculine characters
		}
	}

	public void applyTattoos(GameCharacter character, boolean overrideExistingTattoos) {
//		if(character.isShy() || character.isInnocent() || character.isPrude()) {
//			return; // Shy, innocent, and prude characters don't get tattoos
//		}
		// Higher corruption levels increase tattoo chance:
		float tattooChance = 0;
		boolean lewdTattoos = false;
		int maxTattooCount = 1;
		switch(character.getCorruptionLevel()) {
			case ZERO_PURE:
				tattooChance = 0.05f;
				break;
			case ONE_VANILLA:
				tattooChance = 0.1f;
				break;
			case TWO_HORNY:
				tattooChance = 0.2f;
				maxTattooCount = 2;
				break;
			case THREE_DIRTY:
				tattooChance = 0.3f;
				lewdTattoos = true;
				maxTattooCount = 3;
				break;
			case FOUR_LUSTFUL:
				tattooChance = 0.4f;
				lewdTattoos = true;
				maxTattooCount = 5;
				break;
			case FIVE_CORRUPT:
				tattooChance = 0.5f;
				lewdTattoos = true;
				maxTattooCount = 8;
				break;
		}
		// Additional conditions for increased tattoo chance
		if (character.isSlave()) {
			tattooChance += 0.20f;
		}
		if (character.isMasochist()) {
			tattooChance += 0.10f;
		}
		if (character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()) {
			tattooChance += 0.10f;
		}
		if (character.getRace().getDisposition() == Disposition.SAVAGE || character.getRace().getDisposition() == Disposition.UNPREDICTABLE) {
			tattooChance += 0.15f;
		}
		// Decreased chance for shy/prude to get tattoos
		if(character.isShy() || character.isInnocent() || character.isPrude()) {
			tattooChance = 0.8f*tattooChance - 0.05f;
		}
		if(Math.random()>tattooChance) {
			return;
		}

		// Add tattoos:
		// Colour selection:
		Map<List<Colour>, Integer> colourMap = new HashMap<>();
		colourMap.put(Util.newArrayListOfValues(PresetColour.CLOTHING_BLACK, PresetColour.CLOTHING_BLACK, PresetColour.CLOTHING_BLACK), 2);
		colourMap.put(Util.newArrayListOfValues(PresetColour.CLOTHING_RED_DARK, PresetColour.CLOTHING_RED, PresetColour.CLOTHING_RED_VERY_DARK), 1);
		colourMap.put(Util.newArrayListOfValues(PresetColour.CLOTHING_WHITE, PresetColour.CLOTHING_WHITE, PresetColour.CLOTHING_WHITE), 1);
		if(character.isFeminine()) {
			colourMap.put(Util.newArrayListOfValues(PresetColour.CLOTHING_PINK, PresetColour.CLOTHING_PINK_LIGHT, PresetColour.CLOTHING_PURPLE), 1);
		} else {
			colourMap.put(Util.newArrayListOfValues(PresetColour.CLOTHING_BLUE, PresetColour.CLOTHING_BLUE_LIGHT, PresetColour.CLOTHING_BLUE_NAVY), 1);
		}
		List<Colour> colourList = Util.getRandomObjectFromWeightedMap(colourMap);

		List<AbstractTattooType> availableTypes = new ArrayList<>(TattooType.getAllTattooTypes());
		availableTypes.remove(TattooType.getTattooTypeFromId("innoxia_misc_none"));
		availableTypes.removeIf(tattoo->tattoo.isUnique());

		// Make some tattoos a bit less frequent
		availableTypes.removeIf(tattoo->tattoo.getId().contains("kingdoggo") && Math.random()<0.65); // kingdoggo's runic tattoos
		availableTypes.removeIf(tattoo->tattoo.getId().contains("nhom_racetats") && Math.random()<0.65); // nhom species icon tattoos

		// Make some tattoos more frequent based on sexual orientation
		switch (character.getSexualOrientation()) {
			case ANDROPHILIC -> {
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_weapon_crossed_blades"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_knot_trinity"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_knot_spiral"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_symbol_tribal"));
			}
			case AMBIPHILIC -> {
			}
			case GYNEPHILIC -> {
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_hearts_hearts"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_animal_butterflies"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_plant_rose"));
				availableTypes.add(TattooType.getTattooTypeFromId("innoxia_plant_flowers"));
			}
		}
		// Species and corruption-based tattoo types
		if (character.getCorruptionLevel().compareTo(CorruptionLevel.THREE_DIRTY) >= 0
				|| character.getRace() == Race.DEMON) {
			availableTypes.add(TattooType.getTattooTypeFromId("innoxia_symbol_pentagram"));
		}
		if (character.getRace() == Race.HORSE_MORPH) {
			availableTypes.add(TattooType.getTattooTypeFromId("innoxia_animal_hoof"));
		}
		if (character.getRace() == Race.RAT_MORPH) {
			availableTypes.add(TattooType.getTattooTypeFromId("innoxia_gang_rat_skull"));
		}
		if (character.getRace().getDisposition() == Disposition.SAVAGE
				|| character.getRace().getDisposition() == Disposition.UNPREDICTABLE
				|| character.isFeral()) {
			availableTypes.add(TattooType.getTattooTypeFromId("innoxia_animal_paw"));
		}

		// From NHOM's subspecies tattoo mod
		/*String raceTatId = "nhom_racetats_"+character.getSubspecies().getName(null);
		AbstractTattooType raceTat = TattooType.getTattooTypeFromId(raceTatId);
		// Add if an exact match is found
		if (raceTatId.equals(raceTat.getName())) {
		    availableTypes.add(raceTat);
		    System.err.println("Found "+raceTatId);
		}*/

		List<InventorySlot> commonSlots = new ArrayList<>(InventorySlot.getCommonTattooSlots());

		int tattooCount = 1 + Util.random.nextInt(maxTattooCount);
//		System.out.println("Tattoos applied: "+tattooCount+"/"+maxTattooCount);
		for(int i=0; i<tattooCount; i++) {
			if(availableTypes.isEmpty()) {
				break;
			}
			// Type selection:
			AbstractTattooType tattooType = Util.randomItemFrom(availableTypes);
			if(tattooType.isLimitedSlotAvailability()) {
				availableTypes.remove(tattooType);
			}
			// Slot selection:
			List<InventorySlot> slotAvailability = tattooType.getSlotAvailability();
			slotAvailability.retainAll(commonSlots);
			if(!slotAvailability.isEmpty()) {
				InventorySlot slot = Util.randomItemFrom(slotAvailability);
				commonSlots.remove(slot);
				// Writing selection:
				String writing = "";
				if(Math.random()<0.1f && lewdTattoos) {
					if(slot==InventorySlot.GROIN && character.hasVagina()) {
						writing = Util.randomItemFrom(Util.newArrayListOfValues(
								character.getFetishDesire(Fetish.FETISH_PREGNANCY).isPositive()?"Breed me!":null,
								"Fuck me!",
								"Hot Pussy"));

					} else {
						writing = Util.randomItemFrom(Util.newArrayListOfValues(
								character.getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive()?"Dominate me":null,
								character.getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive()?"Good pet":null,
								character.getFetishDesire(Fetish.FETISH_DOMINANT).isPositive()?"Bow down":null,
								character.getFetishDesire(Fetish.FETISH_DOMINANT).isPositive()?"Submit to me":null));
					}
				}
				TattooWriting tattooWriting = null;
				if(writing!=null && !writing.isEmpty()) {
					tattooWriting = new TattooWriting(
							writing,
							colourList.get(1),
							Math.random()<0.1f, // Glow
							character.isFeminine()&&Math.random()<0.5f?TattooWritingStyle.ITALICISED:null);
				}
				Tattoo tat = new Tattoo(
						tattooType,
						colourList.get(0),
						colourList.get(1),
						colourList.get(2),
						Math.random()<0.1f || (tattooWriting!=null && tattooWriting.isGlow()),
						tattooWriting,
						null);
				character.addTattoo(slot, tat);
			}
		}

		// Special womb tattoo chance
		double wombTatChance = tattooChance * 0.65;
		wombTatChance += character.getFetishDesire(Fetish.FETISH_PREGNANCY).isPositive() ? 0.15 : -0.05;
		wombTatChance += character.getFetishDesire(Fetish.FETISH_PREGNANCY) == FetishDesire.FOUR_LOVE ? 0.20 : 0;
		wombTatChance += character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive() ? 0.05 : -0.05;
		wombTatChance += character.hasTraitActivated(Perk.FETISH_BROODMOTHER) ? 0.15 : 0;
		wombTatChance *= character.getFetishDesire(Fetish.FETISH_PREGNANCY).isPositive() ? 2 : 0.75;
		wombTatChance *= character.getFetishDesire(Fetish.FETISH_PREGNANCY).isNegative() ? 0 : 1;
		wombTatChance *= character.isAbleToBeImpregnated() ? 1.2 : 0.5;
		wombTatChance *= character.hasVagina() ? 1 : 0.5;

		if(Math.random()< wombTatChance) {
			boolean pregSub = character.getFetishDesire(Fetish.FETISH_SUBMISSIVE).isPositive();
			boolean pregDom = character.getFetishDesire(Fetish.FETISH_DOMINANT).isPositive();
			boolean cumDump = character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()||character.getFetishDesire(Fetish.FETISH_CUM_ADDICT).isPositive();
			boolean fem = character.isFeminine();
			boolean masc = !fem;
			boolean masochist = character.isMasochist();
			boolean cherry = character.isVaginaVirgin();
			boolean slave = character.isSlave();
			String writing = Util.randomItemFrom(Util.newArrayListOfValues(
					"",
					"",
					"Breed me",
					"Breeder",
					"Breedable",
					"No Condoms",
					"Cum Inside",

					fem?"Mommy":null,
					masc?"Daddy":null,
					"Make Me A "+(fem?"Mommy":"Daddy"),

					pregSub?"Breeding Bitch":null,
					pregSub?"Pregslut":null,
					pregSub?"Wombslut":null,
					pregSub?"Submissive and Breedable":null,
					pregSub?"Use Me":null,
					pregSub&&masc?"Bitchboy":null,
					pregSub&&masc?"Omega":null,
					pregDom?"Worship":null,
					pregDom?"Give Tribute":null,
					pregDom?"Alpha Breeder":null,
					pregDom&&fem?"Goddess":null,
					pregDom&&fem?"Mommy":null,
					pregDom&&masc?"Master Breeder":null,
					pregDom&&masc?"Daddy":null,
					pregDom&&masochist?"Prove Your Worth":null,
					masochist?"Bastards On Board":null,
					masochist?"Use Me":null,

					character.getFetishDesire(Fetish.FETISH_SIZE_QUEEN).isPositive()?"Breed Me Deep":null,
					character.getFetishDesire(Fetish.FETISH_SIZE_QUEEN).isPositive()?"Hilt Me":null,
					character.getFetishDesire(Fetish.FETISH_SIZE_QUEEN).isPositive()?"All The Way In":null,
					character.getFetishDesire(Fetish.FETISH_CUM_ADDICT).isPositive()?"Fill Me Up":null,
					character.getFetishDesire(Fetish.FETISH_CUM_ADDICT).isPositive()?"Sperm Addict":null,
					character.getFetishDesire(Fetish.FETISH_LACTATION_SELF).isPositive()&&fem?"Milky Mommy":null,
					character.getFetishDesire(Fetish.FETISH_LACTATION_SELF).isPositive()?"Milky":null,
					character.getFetishDesire(Fetish.FETISH_PURE_VIRGIN).isPositive()&&cherry?"Pure":null,
					character.getFetishDesire(Fetish.FETISH_PURE_VIRGIN).isPositive()&&!cherry?"Impure":null,
					character.getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive()?"Fucktoy":null,
					character.getFetishDesire(Fetish.FETISH_NON_CON_SUB).isPositive()?"Fuckmeat":null,
					character.getFetishDesire(Fetish.FETISH_BONDAGE_VICTIM).isPositive()?"Bound Breeder":null,
					character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()?"Sexual Education":null,
					character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()?"Make Bets":null,
					character.getFetishDesire(Fetish.FETISH_EXHIBITIONIST).isPositive()?"Watch Me Swell":null,
					character.getFetishDesire(Fetish.FETISH_BIMBO).isPositive()&&fem?"Bimbo":null,
					character.getFetishDesire(Fetish.FETISH_BIMBO).isPositive()&&fem?"Bimbo Breeder":null,
					character.getFetishDesire(Fetish.FETISH_BIMBO).isPositive()&&masc?"Cum In Me Bro":null,
					character.getFetishDesire(Fetish.FETISH_BIMBO).isPositive()&&masc?"Breed Me Bro":null,
					character.getFetishDesire(Fetish.FETISH_BIMBO).isPositive()&&masc?"Brofessional Breeder":null,
					character.getFetishDesire(Fetish.FETISH_LUSTY_MAIDEN).isPositive()?"Maiden":null,
					cumDump?"Public Breeder":null,
					cumDump?"Cum Dumpster":null,
					cumDump?"Public Service":null,

					character.hasTraitActivated(Perk.FETISH_BROODMOTHER)?"Broodmother":null,
					character.hasTraitActivated(Perk.FETISH_BROODMOTHER)?"Extra Fertile":null,
					character.hasTraitActivated(Perk.FETISH_BROODMOTHER)?"High Capacity":null,
					character.hasTraitActivated(Perk.FETISH_BROODMOTHER)?"Litter Factory":null,
					character.isFeral()?"Bestial Breeder":null,
					character.isFeral()?"Beast Bitch":null,
					character.isFeral()?"Feral Bitch":null,
					cherry?"For The Worthy":null,

					slave?"Breeding Slave":null,
					slave?"Master's Bitch":null,
					slave?"Use Me":null,
					slave&&fem?"Slave Bitch":null,
					slave&&masc?"Slave Bitchboy":null
			));
			TattooWriting wombWriting = null;
			if(writing!=null && !writing.isEmpty()) {
				wombWriting = new TattooWriting(
						writing,
						Util.randomItemFrom(Util.newArrayListOfValues(
								PresetColour.CLOTHING_RED,
								PresetColour.CLOTHING_BLACK,
								PresetColour.CLOTHING_PINK_DARK,
								PresetColour.CLOTHING_BLUE_DARK,
								PresetColour.CLOTHING_PURPLE,
								PresetColour.CLOTHING_PURPLE_ROYAL,
								character.isFeminine()?PresetColour.CLOTHING_PINK:null,
								character.isFeminine()?PresetColour.CLOTHING_PINK_DARK:null,
								!character.isFeminine()?PresetColour.CLOTHING_BLUE:null,
								!character.isFeminine()?PresetColour.CLOTHING_BLUE_DARK:null,
								PresetColour.CLOTHING_GREEN
						)),
						Math.random()<0.1f, // Glow
						character.isFeminine()&&Math.random()<0.5f?TattooWritingStyle.ITALICISED:null);
			} //
			Tattoo wombTat = new Tattoo(
					TattooType.getTattooTypeFromId("innoxia_heartWomb_heart_womb"),
					Math.random()<0.1f || (wombWriting!=null && wombWriting.isGlow()),
					wombWriting,
					null);
			character.addTattoo(InventorySlot.GROIN, wombTat);
		}
	}

	public void applyDirtiness(GameCharacter character) {
		ArrayList<InventorySlot> validSlots = new ArrayList<>();

		for(InventorySlot slot : InventorySlot.getClothingSlots()) {
			if(slot.getBodyPartClothingBlock(character) == null) {
				validSlots.add(slot);
			}
		}

		int dirtySlots = Util.random.nextInt(validSlots.size()/2) + 1;

		for(int i = 0; i < dirtySlots; i++) {
			InventorySlot slot = Util.randomItemFrom(validSlots);
			character.inventory.addDirtySlot(slot);
			validSlots.remove(slot);
		}
	}

	public List<AbstractClothing> generateEnchantedClothingForTrader(GameCharacter trader, List<AbstractClothing> clothingToSell, int numberOfUncommonsToGenerate, int numberofRaresToGenerate) {

		List<AbstractClothing> clothingGenerated = new ArrayList<>();
		List<AbstractClothingType> enchantedClothingTypes = new ArrayList<>();

		clothingToSell.forEach(c -> {
			if(c.getClothingType().getEffects().isEmpty() && c.getClothingType().getRarity()==Rarity.COMMON)
				enchantedClothingTypes.add(c.getClothingType());
		});
		for(int i=0; i<numberOfUncommonsToGenerate; i++) { // Add 'numberOfUncommonsToGenerate' items of enchanted clothing:
			if(enchantedClothingTypes.isEmpty()) {
				break;
			}
			AbstractClothingType type = Util.randomItemFrom(enchantedClothingTypes);
			enchantedClothingTypes.remove(type);
			AbstractClothing c = Main.game.getItemGen().generateClothingWithEnchantment(type);
			c.setEnchantmentKnown(trader, true);
			clothingGenerated.add(c);
		}
		for(int i=0; i<numberofRaresToGenerate; i++) { // Add 'numberofRaresToGenerate' items of extra-enchanted clothing:
			if(enchantedClothingTypes.isEmpty()) {
				break;
			}
			AbstractClothingType type = Util.randomItemFrom(enchantedClothingTypes);
			enchantedClothingTypes.remove(type);
			AbstractClothing c = Main.game.getItemGen().generateRareClothing(type);
			c.setEnchantmentKnown(trader, true);
			clothingGenerated.add(c);
		}

		return clothingGenerated;
	}
}
