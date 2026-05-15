package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.BusinessException;
import com.zhantu.common.Result;
import com.zhantu.entity.Banner;
import com.zhantu.entity.Category;
import com.zhantu.entity.Orders;
import com.zhantu.entity.Product;
import com.zhantu.entity.User;
import com.zhantu.service.BannerService;
import com.zhantu.service.CategoryService;
import com.zhantu.service.OrderItemService;
import com.zhantu.service.OrdersService;
import com.zhantu.service.ProductService;
import com.zhantu.service.UserService;
import com.zhantu.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理后台模块")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrdersService ordersService;
    private final OrderItemService orderItemService;
    private final BannerService bannerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        User user = userService.lambdaQuery().eq(User::getPhone, username).one();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "无管理员权限");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getPhone());
        result.put("token", jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole()));
        result.put("role", user.getRole());
        return Result.success(result);
    }

    @PostMapping("/register")
    @Operation(summary = "管理员注册")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");

        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (username.trim().length() < 3 || username.trim().length() > 20) {
            throw new BusinessException(400, "用户名长度需在3-20个字符之间");
        }
        if (password == null || password.isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }
        if (password.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }

        User existingUser = userService.lambdaQuery().eq(User::getPhone, username).one();
        if (existingUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        User user = new User();
        user.setPhone(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ADMIN");
        user.setStatus(1);
        userService.save(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getPhone());
        result.put("token", jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole()));
        result.put("role", user.getRole());
        return Result.success(result);
    }

    @GetMapping("/product/page")
    @Operation(summary = "商品分页查询")
    public Result<IPage<Product>> getProductPage(@RequestParam(required = false) Long categoryId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.getAdminProductPage(categoryId, keyword, null, pageNum, pageSize));
    }

    @PostMapping("/product")
    @Operation(summary = "新增商品")
    public Result<Void> addProduct(@RequestBody Product product) {
        productService.save(product);
        return Result.success();
    }

    @PutMapping("/product")
    @Operation(summary = "编辑商品")
    public Result<Void> updateProduct(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/product/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }

    @PutMapping("/product/status/{id}")
    @Operation(summary = "商品上下架")
    public Result<Void> updateProductStatus(@PathVariable Long id,
                                             @RequestParam Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productService.updateById(product);
        return Result.success();
    }

    @PutMapping("/product/stock/{id}")
    @Operation(summary = "调整库存(绝对值)")
    public Result<Void> updateStock(@PathVariable Long id,
                                     @RequestParam Integer stock) {
        Product product = new Product();
        product.setId(id);
        product.setStock(stock);
        productService.updateById(product);
        return Result.success();
    }

    @PostMapping("/product/stock-in/{id}")
    @Operation(summary = "商品入库")
    public Result<Void> stockIn(@PathVariable Long id,
                                 @RequestParam Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(400, "入库数量必须大于0");
        }
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        product.setStock(product.getStock() + quantity);
        productService.updateById(product);
        return Result.success();
    }

    @PostMapping("/product/stock-out/{id}")
    @Operation(summary = "商品出库")
    public Result<Void> stockOut(@PathVariable Long id,
                                  @RequestParam Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(400, "出库数量必须大于0");
        }
        Product product = productService.getById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (product.getStock() < quantity) {
            throw new BusinessException(400, "库存不足，当前库存: " + product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        productService.updateById(product);
        return Result.success();
    }

    @GetMapping("/category/tree")
    @Operation(summary = "分类树")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @PostMapping("/category")
    @Operation(summary = "新增分类")
    public Result<Void> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping("/category")
    @Operation(summary = "编辑分类")
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/category/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        long childCount = categoryService.lambdaQuery().eq(Category::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException(400, "该分类下存在子分类，无法删除");
        }
        long productCount = productService.lambdaQuery().eq(Product::getCategoryId, id).count();
        if (productCount > 0) {
            throw new BusinessException(400, "该分类下存在商品，无法删除");
        }
        categoryService.removeById(id);
        return Result.success();
    }

    @GetMapping("/order/page")
    @Operation(summary = "订单分页查询")
    public Result<IPage<Orders>> getOrderPage(@RequestParam(required = false) Integer status,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Orders> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        IPage<Orders> result = ordersService.page(page, wrapper);
        if (!result.getRecords().isEmpty()) {
            List<Long> orderIds = result.getRecords().stream()
                    .map(Orders::getId)
                    .collect(java.util.stream.Collectors.toList());
            List<com.zhantu.entity.OrderItem> allItems = orderItemService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.zhantu.entity.OrderItem>()
                            .in(com.zhantu.entity.OrderItem::getOrderId, orderIds));
            Map<Long, List<com.zhantu.entity.OrderItem>> itemMap = allItems.stream()
                    .collect(java.util.stream.Collectors.groupingBy(com.zhantu.entity.OrderItem::getOrderId));
            for (Orders order : result.getRecords()) {
                order.setItems(itemMap.getOrDefault(order.getId(), java.util.Collections.emptyList()));
            }
        }
        return Result.success(result);
    }

    @GetMapping("/order/{id}")
    @Operation(summary = "订单详情")
    public Result<Orders> getOrderDetail(@PathVariable Long id) {
        Orders order = ordersService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        java.util.List<com.zhantu.entity.OrderItem> items = orderItemService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.zhantu.entity.OrderItem>()
                        .eq(com.zhantu.entity.OrderItem::getOrderId, id));
        order.setItems(items);
        return Result.success(order);
    }

    @PutMapping("/order/{id}/status")
    @Operation(summary = "更新订单状态")
    public Result<Void> updateOrderStatus(@PathVariable Long id,
                                           @RequestBody Map<String, Object> params) {
        Orders order = ordersService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        Integer status = (Integer) params.get("status");
        order.setStatus(status);
        ordersService.updateById(order);
        return Result.success();
    }

    @PutMapping("/order/{id}/ship")
    @Operation(summary = "管理员发货")
    public Result<Void> shipOrder(@PathVariable Long id,
                                   @RequestParam(required = false) String logisticsCompany,
                                   @RequestParam(required = false) String logisticsNo) {
        Orders order = ordersService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(400, "只有待发货状态的订单才能发货");
        }
        order.setStatus(2);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setShipTime(java.time.LocalDateTime.now());
        ordersService.updateById(order);
        return Result.success();
    }

    @PutMapping("/order/{id}/cancel")
    @Operation(summary = "管理员取消订单")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        Orders order = ordersService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "只有待付款状态的订单才能取消");
        }
        order.setStatus(4);
        ordersService.updateById(order);
        return Result.success();
    }

    @GetMapping("/order/{id}/logistics")
    @Operation(summary = "查询物流信息")
    public Result<Map<String, Object>> getLogistics(@PathVariable Long id) {
        Orders order = ordersService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getLogisticsCompany() == null && order.getLogisticsNo() == null) {
            throw new BusinessException(400, "该订单暂无物流信息");
        }
        Map<String, Object> logistics = new HashMap<>();
        logistics.put("orderNo", order.getOrderNo());
        logistics.put("logisticsCompany", order.getLogisticsCompany());
        logistics.put("logisticsNo", order.getLogisticsNo());
        logistics.put("shipTime", order.getShipTime());
        logistics.put("receiverName", order.getReceiverName());
        logistics.put("receiverPhone", order.getReceiverPhone());
        logistics.put("receiverAddress", order.getReceiverAddress());
        logistics.put("status", order.getStatus());
        return Result.success(logistics);
    }

    @GetMapping("/user/page")
    @Operation(summary = "用户分页查询")
    public Result<IPage<User>> getUserPage(@RequestParam(required = false) String keyword,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getPhone, keyword).or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> result = userService.page(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @PutMapping("/user/status/{id}")
    @Operation(summary = "更新用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @RequestBody Map<String, Integer> params) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(params.get("status"));
        userService.updateById(user);
        return Result.success();
    }

    @PutMapping("/user/reset-password/{id}")
    @Operation(summary = "重置用户密码")
    public Result<Map<String, String>> resetPassword(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        String newPassword = generateRandomPassword(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        Map<String, String> result = new HashMap<>();
        result.put("newPassword", newPassword);
        return Result.success(result);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(length);
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @GetMapping("/stats")
    @Operation(summary = "数据统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("productCount", productService.count());
        stats.put("userCount", userService.count());
        stats.put("orderCount", ordersService.count());
        stats.put("categoryCount", categoryService.count());

        long todayOrders = ordersService.lambdaQuery()
                .ge(Orders::getCreateTime, java.time.LocalDate.now().atStartOfDay())
                .count();
        stats.put("todayOrders", todayOrders);

        long pendingOrders = ordersService.lambdaQuery().eq(Orders::getStatus, 1).count();
        stats.put("pendingOrders", pendingOrders);

        long pendingRefunds = ordersService.lambdaQuery().eq(Orders::getStatus, 5).count();
        stats.put("pendingRefunds", pendingRefunds);

        long lowStockProducts = productService.lambdaQuery().lt(Product::getStock, 10).count();
        stats.put("lowStockProducts", lowStockProducts);

        java.math.BigDecimal todayRevenue = ordersService.lambdaQuery()
                .ge(Orders::getCreateTime, java.time.LocalDate.now().atStartOfDay())
                .in(Orders::getStatus, 1, 2, 3)
                .list()
                .stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        stats.put("todayRevenue", todayRevenue);

        java.time.LocalDate today = java.time.LocalDate.now();
        List<Map<String, Object>> salesTrend = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            java.math.BigDecimal dayRevenue = ordersService.lambdaQuery()
                    .ge(Orders::getCreateTime, date.atStartOfDay())
                    .lt(Orders::getCreateTime, date.plusDays(1).atStartOfDay())
                    .in(Orders::getStatus, 1, 2, 3)
                    .list()
                    .stream()
                    .map(o -> o.getPayAmount() != null ? o.getPayAmount() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            long dayOrders = ordersService.lambdaQuery()
                    .ge(Orders::getCreateTime, date.atStartOfDay())
                    .lt(Orders::getCreateTime, date.plusDays(1).atStartOfDay())
                    .count();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("revenue", dayRevenue);
            dayData.put("orders", dayOrders);
            salesTrend.add(dayData);
        }
        stats.put("salesTrend", salesTrend);

        List<Map<String, Object>> topProducts = orderItemService.lambdaQuery()
                .select(com.zhantu.entity.OrderItem::getProductName,
                        com.zhantu.entity.OrderItem::getProductId)
                .list()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        com.zhantu.entity.OrderItem::getProductName,
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
        stats.put("topProducts", topProducts);

        List<Map<String, Object>> userGrowth = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            long newUsers = userService.lambdaQuery()
                    .ge(User::getCreateTime, date.atStartOfDay())
                    .lt(User::getCreateTime, date.plusDays(1).atStartOfDay())
                    .count();
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("count", newUsers);
            userGrowth.add(dayData);
        }
        stats.put("userGrowth", userGrowth);

        return Result.success(stats);
    }

    @GetMapping("/notifications")
    @Operation(summary = "获取待处理通知数量")
    public Result<Map<String, Object>> getNotifications() {
        Map<String, Object> notifications = new HashMap<>();
        long pendingOrders = ordersService.lambdaQuery().eq(Orders::getStatus, 1).count();
        long pendingRefunds = ordersService.lambdaQuery().eq(Orders::getStatus, 5).count();
        long lowStockProducts = productService.lambdaQuery().lt(Product::getStock, 10).count();
        notifications.put("pendingOrders", pendingOrders);
        notifications.put("pendingRefunds", pendingRefunds);
        notifications.put("lowStockProducts", lowStockProducts);
        notifications.put("total", pendingOrders + pendingRefunds + lowStockProducts);
        return Result.success(notifications);
    }

    @GetMapping("/banners")
    @Operation(summary = "获取轮播图列表")
    public Result<List<Banner>> getBanners() {
        return Result.success(bannerService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Banner>()
                        .orderByAsc(Banner::getSort)));
    }

    @PostMapping("/banner")
    @Operation(summary = "新增轮播图")
    public Result<Void> addBanner(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success();
    }

    @PutMapping("/banner")
    @Operation(summary = "编辑轮播图")
    public Result<Void> updateBanner(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success();
    }

    @DeleteMapping("/banner/{id}")
    @Operation(summary = "删除轮播图")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }
}
