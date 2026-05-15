-- 战途汽配商城 - 补充变速箱油数据导入脚本
-- 来源：微信图片_20260121163924_29_9.jpg

USE autoparts;

-- ============================================
-- 全合成变速箱油 (category_id = 3)
-- ============================================

-- ATF 5S (4-5速自动变速)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-5S-1L12', '全合成变速箱油 ATF 5S', '适用于4-5速自动变速器', NULL, 19.30, 999, 3, '丰田/本田/现代/马自达/大众/雷克萨斯/三菱', 'ATF 5S', '1L*12', '1L*12', '桶', '通用');

-- ATF 6SP (6速自动变速器)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-6SP-1L12', '全合成变速箱油 ATF 6SP', '适用于6速自动变速器', NULL, 24.70, 999, 3, '丰田/本田/现代/马自达/大众/雷克萨斯/三菱', 'ATF 6SP', '1L*12', '1L*12', '桶', '通用');

-- ATF VI (高级车型6速)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-VI-1L12', '全合成变速箱油 ATF VI', '高级车型6速有级线性电磁阀控制变速器', NULL, 24.70, 999, 3, '通用', 'ATF VI', '1L*12', '1L*12', '桶', '通用');

-- ATF 6HP (ZF 6HP系列6速)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-6HP-1L12', '全合成变速箱油 ATF 6HP', '德系车后驱动ZF6HP系列6速有级智能变速器', NULL, 24.90, 999, 3, '德系车', 'ATF 6HP', '1L*12', '1L*12', '桶', '通用');

-- ATF 8HP (ZF 8HP系列8速)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-8HP-1L12', '全合成变速箱油 ATF 8HP', '德系车后驱动ZF8HP系列8速有级智能变速器', NULL, 27.70, 999, 3, '德系车', 'ATF 8HP', '1L*12', '1L*12', '桶', '通用');

-- ATF 8SP (奥迪Q7/保时捷/途锐3.0T)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-8SP-1L12', '全合成变速箱油 ATF 8SP', '适用于奥迪Q7/保时捷/途锐3.0T', NULL, 27.70, 999, 3, '奥迪/保时捷/大众', 'ATF 8SP', '1L*12', '1L*12', '桶', '通用');

-- ATF 9HP (极光路虎/丰田/自由光/捷豹14年后)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-9HP-1L12', '全合成变速箱油 ATF 9HP', '适用于极光路虎/丰田/自由光/捷豹14年后', NULL, 34.00, 999, 3, '路虎/丰田/Jeep/捷豹', 'ATF 9HP', '1L*12', '1L*12', '桶', '通用');

-- ATF 722.9 (奔驰系列722.9)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('ATF-7229-1L12', '全合成变速箱油 ATF 722.9', '用于奔驰系列722.9二代含1代7速有机智能变速器', NULL, 28.00, 999, 3, '奔驰', 'ATF 722.9', '1L*12', '1L*12', '桶', '通用');

-- ============================================
-- 全合成无极变速箱油
-- ============================================

-- CVT NS-3 (日产/斯巴鲁/三菱/雷诺)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('CVT-NS3-1L12', '全合成无极变速箱油 CVT NS-3', '捷特科非标专用无极CVT结构', NULL, 28.00, 999, 3, '日产/斯巴鲁/三菱/雷诺', 'CVT NS-3', '1L*12', '1L*12', '桶', '通用');

-- CVT F2 (日系车/邦奇/宝马MINI/奔驰B系)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('CVT-F2-1L12', '全合成无极变速箱油 CVT F2', '低排量紧凑型无极CVT结构', NULL, 28.00, 999, 3, '日系车/邦奇/宝马/奔驰', 'CVT F2', '1L*12', '1L*12', '桶', '通用');

-- CVT+ (德系车无极CVT)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('CVT-PLUS-1L12', '全合成无极变速箱油 CVT+', '德系车无极CVT变速器含模拟档6速7速', NULL, 28.00, 999, 3, '德系车', 'CVT+', '1L*12', '1L*12', '桶', '通用');

-- ============================================
-- 全合成双离合变数箱油
-- ============================================

-- DCT F+ (所有湿式双离合)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('DCT-FP-1L12', '全合成双离合变数箱油 DCT F+', '所有湿式双离合变速器系列用油', NULL, 28.00, 999, 3, '通用', 'DCT F+', '1L*12', '1L*12', '桶', '通用');

-- ============================================
-- 全合成手动变速箱油
-- ============================================

-- MTF75W-90 (大众/福特/比亚迪)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('MTF-75W90-1L12', '全合成手动变速箱油 MTF75W-90', '适用于大众/福特/比亚迪', NULL, 24.90, 999, 3, '大众/福特/比亚迪', 'MTF75W-90', '1L*12', '1L*12', '桶', '通用');

-- ============================================
-- 全合成阀体油
-- ============================================

-- BOODY OIL (干式双离合机电单元用油)
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('BOODY-OIL-1L12', '全合成阀体油 BOODY OIL', '适用于干式双离合机电单元用油', NULL, 24.60, 999, 3, '通用', 'BOODY OIL', '1L*12', '1L*12', '桶', '通用');

-- ============================================
-- 刹车油
-- ============================================

-- DOT4
INSERT INTO `product` (`code`, `name`, `sub_name`, `oem`, `price`, `stock`, `category_id`, `series`, `quality_grade`, `viscosity`, `spec`, `unit`, `brand`) VALUES
('BF-DOT4-500ML12', '刹车油 DOT4', 'DOT4级别刹车油', NULL, 13.50, 999, 3, '通用', 'DOT4', '500ML*12', '500ML*12', '桶', '通用');
