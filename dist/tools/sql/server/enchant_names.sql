DROP TABLE IF EXISTS `enchant_names`;
CREATE TABLE `enchant_names`  (
  `skill_id` int(4) NOT NULL DEFAULT 0,
  `skill_lvl` int(3) NOT NULL DEFAULT 0,
  `enchant_name` varchar(35) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`skill_id`, `skill_lvl`, `enchant_name`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of enchant_names
-- ----------------------------
-- Invigor
INSERT INTO `enchant_names` VALUES (1032, 330,'Power');
-- Resist Poison
INSERT INTO `enchant_names` VALUES (1033, 330,'Power');
-- Mana Regeneration
INSERT INTO `enchant_names` VALUES (1047, 130,'Power');
-- Resist Aqua
INSERT INTO `enchant_names` VALUES (1182, 330,'Power');
-- Resist Wind
INSERT INTO `enchant_names` VALUES (1189, 330,'Power');
-- Resist Fire
INSERT INTO `enchant_names` VALUES (1191, 330,'Power');
-- Blazing Skin
INSERT INTO `enchant_names` VALUES (1232, 130,'Time');
INSERT INTO `enchant_names` VALUES (1232, 230,'Cost');
INSERT INTO `enchant_names` VALUES (1232, 330,'Defence');
INSERT INTO `enchant_names` VALUES (1232, 430,'Resist Fire');
-- Freezing Skin
INSERT INTO `enchant_names` VALUES (1238, 130,'Time');
INSERT INTO `enchant_names` VALUES (1238, 230,'Power');
INSERT INTO `enchant_names` VALUES (1238, 330,'Defence');
INSERT INTO `enchant_names` VALUES (1238, 430,'Resist Water');
-- Resist Shock
INSERT INTO `enchant_names` VALUES (1259, 330,'Power');
-- Prophecy of Water
INSERT INTO `enchant_names` VALUES (1355, 315,'Decrease Penalty');
-- Prophecy of Fire
INSERT INTO `enchant_names` VALUES (1356, 315,'Decrease Penalty');
-- Prophecy of Wind
INSERT INTO `enchant_names` VALUES (1357, 315,'Decrease Penalty');
-- Chant Of Victory
INSERT INTO `enchant_names` VALUES (1363, 315,'Decrease Penalty');
-- Holy Resistence
INSERT INTO `enchant_names` VALUES (1392, 130,'Power');
-- Unholy Resistence
INSERT INTO `enchant_names` VALUES (1393, 130,'Power');
-- Clarity
INSERT INTO `enchant_names` VALUES (1397, 130,'Physical');
INSERT INTO `enchant_names` VALUES (1397, 230,'Magic');
INSERT INTO `enchant_names` VALUES (1397, 330,'Music');
-- Pa'agrio's Fist
INSERT INTO `enchant_names` VALUES (1416, 115,'Power');
-- Resist Earth
INSERT INTO `enchant_names` VALUES (1548, 330,'Power');
