package com.lilithsthrone.game.sex.sexActions.baseActions;

import com.lilithsthrone.game.character.attributes.CorruptionLevel;
import com.lilithsthrone.game.character.body.valueEnums.PenetrationModifier;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.sex.ArousalIncrease;
import com.lilithsthrone.game.sex.SexAreaPenetration;
import com.lilithsthrone.game.sex.SexPace;
import com.lilithsthrone.game.sex.SexParticipantType;
import com.lilithsthrone.game.sex.sexActions.SexAction;
import com.lilithsthrone.game.sex.sexActions.SexActionType;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.Util.Value;

/**
 * @since 0.3.3
 * @version 0.3.3
 * @author Innoxia
 */
public class PenisPenis {
	
	public static final SexAction FROTTING_START = new SexAction(
			SexActionType.START_ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ONE_VANILLA,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Start frotting";
		}

		@Override
		public String getActionDescription() {
			return "Start grinding your [npc.cock] up and down against [npc2.namePos] [npc2.cock].";
		}

		@Override
		public String getDescription() {
			
			UtilText.nodeContentSB.setLength(0);
			
			switch(Main.sex.getSexPace(Main.sex.getCharacterPerformingAction())) {
				case DOM_GENTLE:
				case DOM_NORMAL:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"Reaching down, [npc.name] [npc.verb(adjust)] [npc.her] [npc.cock] to line it up with [npc2.hers], before starting to slowly thrust, rubbing [npc.her] shaft against [npc2.hers].",
							"Carefully adjusting [npc.her] [npc.hips+], [npc.name] [npc.verb(press)] [npc.her] [npc.cock] against [npc2.namePos] [npc2.cock], before starting to rhythmically grind back and forth.",
							"With [npc.a_moan+], [npc.name] slowly [npc.verb(press)] [npc.her] groin in between [npc2.namePos] [npc2.legs], before starting to tease [npc.her] [npc.cock] up and down over [npc2.hers]."));
					break;
				case DOM_ROUGH:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"With a rough buck of [npc.her] [npc.hips+], [npc.name] forcefully [npc.verb(grind)] [npc.her] [npc.cock] against [npc2.namePos] [npc2.cock].",
							"With a growl, [npc.name] roughly [npc.verb(pull)] [npc2.namePos] crotch into place around [npc.her] [npc.legs], lining up [npc.her] [npc.cock] against [npc2.hers] and starting to thrust them against each other.",
							"With [npc.a_moan+], [npc.name] roughly [npc.verb(grind)] [npc.her] groin in between [npc2.namePos] [npc2.legs], before starting to forcefully grind [npc.her] [npc.cock] up and down over [npc2.hers]."));
					break;
				case SUB_EAGER:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"[npc.Name] [npc.verb(draw)] [npc2.name] in close, opening [npc.her] legs to align [npc.her] groin with [npc2.hers]. [npc.Name] [npc.verb(pull)] [npc2.him] in eagerly, coaxing [npc2.name] to thrust and grind [npc2.her] [npc2.cock] against [npc.hers].",
							"With [npc.a_moan+], [npc.name] eagerly [npc.verb(guide)] [npc2.namePos] groin in between [npc.namePos] [npc.legs], before starting to frantically grind [npc.her] [npc.cock] up against [npc2.hers]."));
					break;
				case SUB_NORMAL:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"[npc.Name] [npc.verb(draw)] [npc2.name] in close, opening [npc.her] legs to align [npc.her] groin with [npc2.hers]. [npc.Name] [npc.verb(pull)] [npc2.him] in coyly, coaxing [npc2.name] to thrust and grind [npc2.her] [npc2.cock] against [npc.hers].",
							"With [npc.a_moan+], [npc.name] eagerly [npc.verb(guide)] [npc2.namePos] groin in between [npc.namePos] [npc.legs], before starting tease [npc.her] [npc.cock] up against [npc2.hers]."));
					break;
				case SUB_RESISTING:
					break;
			}
			switch(Main.sex.getSexPace(Main.sex.getCharacterTargetedForSexAction(this))) {
				case DOM_GENTLE:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Gently bucking [npc2.her] own [npc2.hips] in response,"
									+ " [npc2.name] [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(mirror)] the movements of [npc.name] and softly [npc2.verb(rub)] [npc2.her] [npc2.cock] against [npc.hers].",
							" Responding with a gentle buck of [npc2.her] own [npc2.hips], [npc2.name] [npc2.verb(start)] mirroring [npc.namePos] movements, helping to softly rub [npc2.her] [npc2.cock] up and down over [npc.hers]."));
					break;
				case DOM_ROUGH:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Roughly bucking [npc2.her] own [npc2.hips] in response,"
									+ " [npc2.name] [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(mirror)] the movements of [npc.name] and forcefully [npc2.verb(grind)] [npc2.her] [npc2.cock] against [npc.hers].",
							" Responding with a violent buck of [npc2.her] own [npc2.hips], [npc2.name] [npc2.verb(start)] mirroring [npc.namePos] movements, helping to roughly grind [npc2.her] [npc2.cock] up and down over [npc.hers]."));
					break;
				case DOM_NORMAL:
				case SUB_EAGER:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Happily bucking [npc2.her] own [npc2.hips] in response,"
									+ " [npc2.name] [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(mirror)] the movements of [npc.name] and readily [npc2.verb(rub)] [npc2.her] [npc2.cock] against [npc.hers].",
							" Responding with a happy buck of [npc2.her] own [npc2.hips], [npc2.name] [npc2.verb(start)] mirroring [npc.namePos] movements, helping to frantically rub [npc2.her] [npc2.cock] up and down over [npc.hers]."));
					break;
				case SUB_NORMAL:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Bucking [npc2.her] own [npc2.hips] in response,"
									+ " [npc2.name] [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(mirror)] the movements of [npc.name] and [npc2.verb(rub)] [npc2.her] [npc2.cock] against [npc.hers].",
							" Responding with a buck of [npc2.her] own [npc2.hips], [npc2.name] [npc2.verb(start)] mirroring [npc.namePos] movements, helping to rub [npc2.her] [npc2.cock] up and down over [npc.hers]."));
					break;
				case SUB_RESISTING:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Desperately trying to pull away,"
									+ " [npc2.name] [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(plead)] for [npc.name] to get away from [npc2.herHim] and leave [npc2.her] [npc2.cock] alone.",
							" Responding by frantically recoiling from [npc.namePos] unwanted advance, [npc2.name] [npc2.verb(start)] pleading to be left alone, all the while trying to pull [npc2.her] [npc2.cock] away from [npc.namePos]."));
					break;
			}
			
			return UtilText.nodeContentSB.toString();
		}
		
	};
	
	private static String getTargetedCharacterResponse(SexAction action) {
		switch(Main.sex.getSexPace(Main.sex.getCharacterTargetedForSexAction(action))) {
			case SUB_EAGER:
			case DOM_NORMAL:
				UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
						" [npc2.Name] enthusiastically [npc2.verb(respond)] in kind, and [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(rub)] [npc2.her] own [npc2.cock] up and down over [npc.namePos].",
						" [npc2.A_moan+] bursts out from between [npc2.namePos] [npc2.lips+], and, eagerly thrusting out [npc2.her] [npc2.hips], [npc2.she] [npc2.verb(grind)] [npc2.her] [npc2.cock] against [npc.namePos].",
						" [npc2.Moaning] in delight, [npc2.name] eagerly [npc2.verb(thrust)] [npc2.her] [npc2.hips+] out, using [npc.namePos] movements to help grind [npc2.her] own [npc2.cock] against [npc.hers]."));
				break;
			case SUB_RESISTING:
				UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
						" Desperately trying, and failing, to pull away from [npc.namePos] [npc.cock], [npc2.name] desperately [npc2.verb(beg)] for [npc.name] to get away from [npc2.him].",
						" [npc2.A_sob+] bursts out from between [npc2.namePos] [npc2.lips] as [npc2.she] weakly [npc2.verb(try)] to push [npc.name] away from [npc2.her] groin.",
						" [npc2.Sobbing] in distress, and with tears running down [npc2.her] [npc2.face],"
								+ " [npc2.name] weakly [npc2.verb(struggle)] against [npc.name], pleading and crying for [npc.herHim] to get away from [npc2.her] groin."));
				break;
			case SUB_NORMAL:
				UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
						" [npc2.Name] [npc2.verb(respond)] in kind, and [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] [npc2.verb(rub)] [npc2.her] own [npc2.cock] up and down over [npc.hers].",
						" [npc2.A_moan+] bursts out from between [npc2.namePos] [npc2.lips+], and, thrusting out [npc2.her] [npc2.hips], [npc2.she] [npc2.verb(grind)] [npc2.her] [npc2.cock] against [npc.hers].",
						" [npc2.Moaning] in pleasure, [npc2.name] [npc2.verb(thrust)] [npc2.her] [npc2.hips+] out, using [npc.namePos] movements to help grind [npc2.her] own [npc2.cock+] against [npc.hers]."));
				break;
			case DOM_GENTLE:
				UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
						" [npc2.Name] happily [npc2.verb(respond)] in kind, and [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] softly [npc2.verb(rub)] [npc2.her] own [npc2.cock] up and down over [npc.namePos] groin.",
						" [npc2.A_moan+] drifts out from between [npc2.namePos] [npc2.lips+], and, gently thrusting out [npc2.her] [npc2.hips], [npc2.she] [npc2.verb(grind)] [npc2.her] [npc2.cock] against [npc.namePos].",
						" [npc2.Moaning] in pleasure, [npc2.name] gently [npc2.verb(push)] [npc2.her] [npc2.hips+] out, using [npc.namePos] movements to help grind [npc2.her] own [npc2.cock+] against [npc.hers]."));
				break;
			case DOM_ROUGH:
				UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
						" [npc2.Name] forcefully [npc2.verb(respond)] in kind, and [npc2.verb(let)] out [npc2.a_moan+] as [npc2.she] roughly [npc2.verb(grind)] [npc2.her] own [npc2.cock] up and down over [npc.namePos] [npc.cock+].",
						" [npc2.A_moan+] bursts out from between [npc2.namePos] [npc2.lips+], and, roughly thrusting out [npc2.her] [npc2.hips], [npc2.she] forcefully [npc2.verb(grind)] [npc2.her] [npc2.cock] against [npc.hers]",
						" [npc2.Moaning] in delight, [npc2.name] roughly [npc2.verb(thrust)] [npc2.her] [npc2.hips+] out, using [npc.namePos] movements to help force [npc2.her] own [npc2.cock+] against [npc.her] groin."));
				break;
		}
		return "";
	}
	
	public static final SexAction FROTTING_DOM_GENTLE = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ONE_VANILLA,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.DOM_GENTLE) {
		
		@Override
		public String getActionTitle() {
			return "Gentle frotting";
		}

		@Override
		public String getActionDescription() {
			return "Gently slide your [npc.cock] up and down over [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);
			
			UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
					"Letting out a little [npc.moan] with every thrust of [npc.her] [npc.hips], [npc.name] gently [npc.verb(rub)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"[npc.Name] [npc.verb(let)] out a series of soft sighs as [npc.she] gently [npc.verb(rub)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"Sliding [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(let)] out a little [npc.moan] with each pump of [npc.her] [npc.hips]."));

			UtilText.nodeContentSB.append(getTargetedCharacterResponse(this));
					
			return UtilText.nodeContentSB.toString();
		}
	};
	
	public static final SexAction FROTTING_DOM_NORMAL = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ONE_VANILLA,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.DOM_NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Frotting";
		}

		@Override
		public String getActionDescription() {
			return "Continue grinding your [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);

			UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+] with each eager thrust of [npc.her] [npc.hips], [npc.name] passionately [npc.verb(rub)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"[npc.Name] [npc.verb(let)] out a series of lewd [npc.moans] as [npc.she] desperately [npc.verb(grind)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"Eagerly grinding [npc.her] [npc.clit+] over [npc2.namePos] [npc2.vagina+], [npc.name] [npc.verb(let)] out [npc.a_moan+] with each frantic pump of [npc.her] [npc.hips]."));

			UtilText.nodeContentSB.append(getTargetedCharacterResponse(this));
			
			return UtilText.nodeContentSB.toString();
		}
	};
	
	public static final SexAction FROTTING_DOM_ROUGH = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.TWO_HORNY,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.DOM_ROUGH) {

		@Override
		public String getActionTitle() {
			return "Rough frotting";
		}

		@Override
		public String getActionDescription() {
			return "Continue roughly grinding your [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].";
		}
		
		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);

			UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+] with each forceful thrust of [npc.her] [npc.hips], [npc.name] roughly [npc.verb(grind)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"[npc.Name] [npc.verb(let)] out a series of lewd [npc.moans] as [npc.she] roughly [npc.verb(grind)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"Violently grinding [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(let)] out [npc.a_moan+] with each forceful pump of [npc.her] [npc.hips]."));

			UtilText.nodeContentSB.append(getTargetedCharacterResponse(this));
			
			return UtilText.nodeContentSB.toString();
		}
	};
	
	public static final SexAction FROTTING_SUB_NORMAL = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ONE_VANILLA,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.SUB_NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Frotting";
		}

		@Override
		public String getActionDescription() {
			return "Continue rubbing your [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);

			UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+] with each thrust of [npc.her] [npc.hips], [npc.name] [npc.verb(continue)] to rub [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"[npc.Name] [npc.verb(let)] out a series of lewd [npc.moans] as [npc.she] [npc.verb(rub)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"Rubbing [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(let)] out [npc.a_moan+] with each pump of [npc.her] [npc.hips]."));

			UtilText.nodeContentSB.append(getTargetedCharacterResponse(this));
			
			return UtilText.nodeContentSB.toString();
		}
	};
	
	public static final SexAction FROTTING_SUB_EAGER = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ONE_VANILLA,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.SUB_EAGER) {
		
		@Override
		public String getActionTitle() {
			return "Eager frotting";
		}

		@Override
		public String getActionDescription() {
			return "Eagerly grind your [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);

			UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+] with each eager thrust of [npc.her] [npc.hips], [npc.name] passionately [npc.verb(rub)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"[npc.Name] [npc.verb(let)] out a series of lewd [npc.moans] as [npc.she] desperately [npc.verb(grind)] [npc.her] [npc.cock+] up and down over [npc2.namePos] [npc2.cock+].",
					"Eagerly grinding [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(let)] out [npc.a_moan+] with each frantic pump of [npc.her] [npc.hips]."));

			UtilText.nodeContentSB.append(getTargetedCharacterResponse(this));
			
			return UtilText.nodeContentSB.toString();
		}
		
	};
	
	public static final SexAction FROTTING_SUB_RESIST = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.TWO_LOW,
			ArousalIncrease.FOUR_HIGH,
			CorruptionLevel.ZERO_PURE,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL,
			SexPace.SUB_RESISTING) {
		
		@Override
		public String getActionTitle() {
			return "Resist frotting";
		}

		@Override
		public String getActionDescription() {
			return "Try to pull away from [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			UtilText.nodeContentSB.setLength(0);

			switch(Main.sex.getSexPace(Main.sex.getCharacterTargetedForSexAction(this))) {
				case DOM_GENTLE:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"[npc.Name] [npc.verb(try)] to pull [npc.her] groin away from [npc2.nameHers],"
									+ " but [npc.her] efforts prove to be in vain as [npc2.she] quickly [npc2.verb(buck)] [npc2.her] [npc2.hips] forwards, keeping [npc2.her] [npc2.cock+] gently pressed against [npc.her] [npc.cock+].",
							"With [npc.a_sob+], [npc.name] [npc.verb(try)] to pull away from [npc2.name],"
									+ " but [npc2.she] quickly [npc2.verb(buck)] [npc2.her] [npc2.hips] forwards, before continuing to gently rub [npc2.her] [npc2.cock+] against [npc.her] [npc.cock+].",
							"Tears start to well up in [npc.namePos] [npc.eyes], and with [npc.a_sob+], [npc.she] [npc.verb(try)] to pull [npc.her] [npc.cock] away from [npc2.nameHers],"
									+ " but [npc2.she] quickly [npc2.verb(shift)] position, ignoring [npc.her] protests as [npc2.she] gently [npc2.verb(rub)] [npc2.her] [npc2.cock+] up against [npc2.hers]."));
					break;
				case DOM_ROUGH:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"[npc.Name] [npc.verb(try)] to pull [npc.her] groin away from [npc2.nameHers],"
									+ " but [npc.her] efforts prove to be in vain as [npc2.she] roughly [npc2.verb(slam)] [npc2.her] [npc2.hips] forwards, keeping [npc2.her] [npc2.cock+] forcefully ground against [npc.her] [npc.cock+].",
							"With [npc.a_sob+], [npc.name] [npc.verb(try)] to pull away from [npc2.name],"
									+ " but [npc2.she] quickly [npc2.verb(buck)] [npc2.her] [npc2.hips] forwards, before continuing to roughly grind [npc2.her] [npc2.cock+] against [npc.her] [npc.cock+].",
							"Tears start to well up in [npc.namePos] [npc.eyes], and with [npc.a_sob+], [npc.she] [npc.verb(try)] to pull [npc.her] [npc.cock] away from [npc2.nameHers],"
									+ " but [npc2.she] quickly [npc2.verb(shift)] position, ignoring [npc.her] protests as [npc2.she] forcefully [npc2.verb(grind)] [npc2.her] [npc2.cock+] up against [npc2.hers]."));
					break;
				default: // DOM_NORMAL and in case anything goes wrong:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"[npc.Name] [npc.verb(try)] to pull [npc.her] groin away from [npc2.nameHers],"
									+ " but [npc.her] efforts prove to be in vain as [npc2.she] quickly [npc2.verb(buck)] [npc2.her] [npc2.hips] forwards, keeping [npc2.her] [npc2.cock+] pressed against [npc.her] [npc.cock+].",
							"With [npc.a_sob+], [npc.name] [npc.verb(try)] to pull away from [npc2.name],"
									+ " but [npc2.she] quickly [npc2.verb(buck)] [npc2.her] [npc2.hips] forwards, before continuing to eagerly rub [npc2.her] [npc2.cock+] against [npc.her] [npc.cock+].",
							"Tears start to well up in [npc.namePos] [npc.eyes], and with [npc.a_sob+], [npc.she] [npc.verb(try)] to pull [npc.her] [npc.pussy] away from [npc2.nameHers],"
									+ " but [npc2.she] quickly [npc2.verb(shift)] position, ignoring [npc.her] protests as [npc2.she] enthusiastically [npc2.verb(rub)] [npc2.her] [npc2.cock+] up against [npc2.hers]."));
					break;
			}
			
			return UtilText.nodeContentSB.toString();
		}
		
	};
	
	public static final SexAction FROTTING_STOP = new SexAction(
			SexActionType.STOP_ONGOING,
			ArousalIncrease.TWO_LOW,
			ArousalIncrease.TWO_LOW,
			CorruptionLevel.ZERO_PURE,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Stop frotting";
		}

		@Override
		public String getActionDescription() {
			return "Pull your [npc.cock+] away from [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public String getDescription() {
			
			UtilText.nodeContentSB.setLength(0);
			
			switch(Main.sex.getSexPace(Main.sex.getCharacterPerformingAction())) {
				case DOM_ROUGH:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"With one last rough thrust, [npc.name] [npc.verb(pull)] [npc.her] groin away from [npc2.namePos] [npc2.cock+], putting an end to the frotting.",
							"Roughly grinding [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock] one last time, [npc.name] then [npc.verb(pull)] back, putting an end to the rough frotting."));
					break;
				default:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							"With one last buck of [npc.her] [npc.hips], [npc.name] [npc.verb(pull)] [npc.her] groin away from [npc2.namePos] [npc2.cock+], putting an end to the frotting.",
							"Rubbing [npc.her] [npc.cock+] over [npc2.namePos] [npc2.cock] one last time, [npc.name] then [npc.verb(pull)] back, putting an end to the frotting."));
					break;
			}
			
			switch(Main.sex.getSexPace(Main.sex.getCharacterTargetedForSexAction(this))) {
				case SUB_RESISTING:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" Although happy to have [npc2.her] [npc2.pussy] released, [npc2.name] [npc2.verb(continue)] crying and weakly struggling against [npc.name] as [npc2.she] [npc2.verb(plead)] for [npc.herHim] to leave [npc2.herHim] alone.",
							" With [npc2.a_sob+], [npc2.name] [npc2.verb(continue)] to struggle and protest, tears streaming down [npc2.her] [npc2.face] as [npc2.she] [npc2.verb(pull)] [npc2.her] [npc2.vagina+] away from [npc.name]."));
					break;
				default:
					UtilText.nodeContentSB.append(UtilText.returnStringAtRandom(
							" [npc2.Name] [npc2.verb(let)] out [npc2.a_moan+] as [npc.name] [npc.verb(pull)] back, signalling [npc2.her] desire for more attention.",
							" [npc2.A_moan+] escapes from between [npc2.namePos] [npc2.lips+], betraying [npc2.her] desperate desire for more of [npc.namePos] attention."));
					break;
			}
			
			return UtilText.nodeContentSB.toString();
		}
	};
	
	public static final SexAction PREHENSILE_FROTTING = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FIVE_EXTREME,
			CorruptionLevel.TWO_HORNY,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Coiled frotting";
		}

		@Override
		public String getActionDescription() {
			return "Use your prehensile cock to wrap around and massage [npc2.namePos] [npc2.cock+].";
		}

		@Override
		public boolean isBaseRequirementsMet() {
			return Main.sex.getCharacterPerformingAction().hasPenisModifier(PenetrationModifier.PREHENSILE) && Main.sex.getSexPace(Main.sex.getCharacterPerformingAction())!=SexPace.DOM_ROUGH;
		}

		@Override
		public String getDescription() {
			return UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+], [npc.name] [npc.verb(concentrate)] on using the prehensility of [npc.her] [npc.cock] to entwine it around [npc2.namePos] [npc2.cock+], before coiling it up to massage and squeeze down on it.",
					"[npc.Name] [npc.verb(let)] out [npc.a_moan+] as [npc.she] [npc.verb(focus)] on controlling [npc.her] prehensile [npc.cock]."
							+ " Wrapping it around [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(grin)] as [npc.she] then [npc.verb(coil)] it up to massage [npc2.her] [npc2.cock+], causing [npc2.herHim] to let out [npc2.a_moan+].",
					"[npc.Name] [npc.verb(grin)] in delight as [npc.she] [npc.verb(use)] [npc.her] prehensile cock to massage and stroke [npc2.namePos] [npc2.cock+], tugging at it with rhythmic undulations.",
					"With [npc.a_moan+], [npc.name] [npc.verb(focus)] on controlling [npc.her] prehensile [npc.cock], wrapping it around [npc2.namePos] [npc2.cock+], trapping and milking it in the coils of [npc.her] constricting [npc.cock].].");
		}
	};
	
	public static final SexAction PREHENSILE_FROTTING_DOUBLE = new SexAction(
			SexActionType.ONGOING,
			ArousalIncrease.FOUR_HIGH,
			ArousalIncrease.FIVE_EXTREME,
			CorruptionLevel.TWO_HORNY,
			Util.newHashMapOfValues(new Value<>(SexAreaPenetration.PENIS, SexAreaPenetration.PENIS)),
			SexParticipantType.NORMAL) {
		
		@Override
		public String getActionTitle() {
			return "Double-coiled frotting";
		}

		@Override
		public String getActionDescription() {
			return "Intertwine your prehensile cock with [npc2.hers].";
		}

		@Override
		public boolean isBaseRequirementsMet() {
			return Main.sex.getTargetedPartner(Main.sex.getCharacterPerformingAction()).hasPenisModifier(PenetrationModifier.PREHENSILE) && Main.sex.getCharacterPerformingAction().hasPenisModifier(PenetrationModifier.PREHENSILE) && Main.sex.getSexPace(Main.sex.getCharacterPerformingAction())!=SexPace.DOM_ROUGH;
		}

		@Override
		public String getDescription() {
			return UtilText.returnStringAtRandom(
					"Letting out [npc.a_moan+], [npc.name] [npc.verb(concentrate)] on using the prehensility of [npc.her] [npc.cock] to entwine it around [npc2.namePos] [npc2.cock+]. [npc2.Name] [npc2.verb(match)] [npc.name], coiling [npc2.her] [npc2.cock] to match [npc.hers], before [npc.name] [npc.verb(start)] to slowly massage and rub at it.",
					"[npc.Name] [npc.verb(let)] out [npc.a_moan+] as [npc.she] [npc.verb(focus)] on controlling [npc.her] prehensile [npc.cock]."
							+ " Wrapping it around [npc2.namePos] [npc2.cock+], [npc.name] [npc.verb(grin)] as [npc.she] then [npc.verb(entice)] [npc2.namePos] [npc2.cock] by stroking and curling around it with [npc.her] own. [npc2.Name] eagerly [npc2.verb(wrap)] [npc2.her] own prehensile [npc2.cock] around [npc.hers], returning the favor with teasing squeezes of [npc2.her] own.",
					"[npc.Name] [npc.verb(grin)] in delight as [npc.she] [npc.verb(use)] [npc.her] prehensile cock to massage and stroke [npc2.namePos] [npc2.cock+], and [npc2.name] [npc2.verb(use)] [npc2.hers] to wrap around [npc.hers]. Tugging at it with [npc.her] undulating [npc.cock], [npc.name] [npc.verb(settle)] on a comfortable rhythm with [npc2.name].",
					"With [npc.a_moan+], [npc.name] [npc.verb(focus)] on controlling [npc.her] prehensile [npc.cock], wrapping it around [npc2.namePos] [npc2.cock+]. Both with prehensile penises, [npc.name] and [npc2.name] try to trap and envelope the other cock, squeezing and milking it in constricting coils.");
		}
	};
}
