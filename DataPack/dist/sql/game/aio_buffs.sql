DROP TABLE IF EXISTS `aio_buffs`;
CREATE TABLE IF NOT EXISTS `aio_buffs` (
  `category` varchar(45) DEFAULT NULL,
  `buff_name` varchar(45) DEFAULT NULL,
  `buff_id` int(10) DEFAULT NULL,
  `buff_lvl` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `aio_buffs` (`category`, `buff_name`, `buff_id`, `buff_lvl`) VALUES
('Prophet','Might', 1068, 3),
('Prophet','Shield', 1040, 3),
('Prophet','Focus', 1077, 3),
('Prophet','Haste', 1086, 2),
('Prophet','Acumen', 1085, 3),
('Prophet','Death Whisper', 1242, 3),
('Prophet','Guidance', 1240, 3),
('Prophet','Mental Shield', 1035, 4),
('Prophet','Blessed Sould', 1048, 6),
('Prophet','Blessed Body', 1045, 6),
('Prophet','Invigor', 1032, 3),
('Prophet','Regeneration', 1044, 3),
('Prophet','Bless Shield', 1243, 6),
('Prophet','Wild Magic', 1303, 2),
('Prophet','Advanced Block', 1304, 3),
('Prophet','Resist Shock', 1259, 4),
('Prophet','Unholy Resistance', 1393, 3),
('Prophet','Clarity', 1397, 3),
('Prophet','Empower', 1059, 3),
('Prophet','Concentration', 1078, 6),
('Prophet','Agility', 1087, 3),
('Prophet','Berserker Spirit', 1062, 2),
('Prophet','Greater Shield', 1389, 3),
('Prophet','Greater Might', 1388, 3),
('Prophet','Holy Resistance', 1392, 3),
('Prophet','Vampiric Rage', 1268, 4),
('Prophet','Elemental Protection', 1352, 3),
('Prophet','Divine Protection', 1353, 3),
('Prophet','Arcane Protection', 1354, 3),
('Prophet','Prophecy of Water', 1355, 1),
('Prophet','Prophecy of Wind', 1357, 1),
('Prophet','Prophecy of Fire', 1356, 1),
('Dances','Dance of Fire', 274, 1),
('Dances','Dance of Light', 277, 1),
('Dances','Dance of Inspiration', 272, 1),
('Dances','Dance of the Mystic', 273, 1),
('Dances','Dance of Concentration', 276, 1),
('Dances','Dance of the Warrior', 271, 1),
('Dances','Dance of Fury', 275, 1),
('Dances','Dance of Earth Guard', 309, 1),
('Dances','Dance of Protection', 311, 1),
('Dances','Dance of Aqua Guard', 307, 1),
('Dances','Dance of Vampire', 310, 1),
('Dances','Siren Dance', 365, 1),
('Dances','Dance of Alignment', 530, 1),
('Songs','Song of Warding', 267, 1),
('Songs','Song of Invocation', 270, 1),
('Songs','Song of Wind', 268, 1),
('Songs','Song of Hunter', 269, 1),
('Songs','Song of Water', 266, 1),
('Songs','Song of Flame War', 306, 1),
('Songs','Song of Vitality', 304, 1),
('Songs','Song of Storm Guard', 308, 1),
('Songs','Song of Vengeance', 305, 1),
('Songs','Song of Renewal', 349, 1),
('Songs','Song of Champion', 364, 1),
('Songs','Song of Meditation', 363, 1);